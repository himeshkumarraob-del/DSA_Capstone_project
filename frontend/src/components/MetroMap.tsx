import React, { useState } from 'react';
import type { Station, RouteResult } from '../services/api';
import { Map, ZoomIn, ZoomOut, RotateCcw } from 'lucide-react';

interface MetroMapProps {
  stations: Station[];
  selectedRoute: RouteResult | null;
  onSelectStation: (stationName: string) => void;
}

export const MetroMap: React.FC<MetroMapProps> = ({
  stations,
  selectedRoute,
  onSelectStation,
}) => {
  const [hoveredStation, setHoveredStation] = useState<Station | null>(null);
  const [zoom, setZoom] = useState<number>(1);

  // Compute SVG coordinates from station latitude/longitude
  const getCoordinates = (lat: number, lon: number): { x: number; y: number } => {
    // Normalization bounds for Chennai dataset
    const minLon = 80.15;
    const maxLon = 80.31;
    const minLat = 12.97;
    const maxLat = 13.18;

    const width = 850;
    const height = 620;
    const padding = 50;

    const x = padding + ((lon - minLon) / (maxLon - minLon)) * (width - 2 * padding);
    const y = padding + ((maxLat - lat) / (maxLat - minLat)) * (height - 2 * padding);

    return { x, y };
  };

  // Station coordinate dictionary
  const stationCoords: Record<number, { x: number; y: number }> = {};
  stations.forEach((st) => {
    stationCoords[st.id] = getCoordinates(st.latitude, st.longitude);
  });

  // Track tracks definition
  const blueLineIds = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15];
  const greenLineIds = [5, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 13, 30];
  const purpleLineIds = [31, 32, 16, 33, 7, 34, 35];

  const buildPathString = (ids: number[]) => {
    const points = ids
      .map((id) => stationCoords[id])
      .filter((pt): pt is { x: number; y: number } => pt !== undefined);

    if (points.length === 0) return '';
    return points.reduce((acc, curr, idx) => {
      return idx === 0 ? `M ${curr.x} ${curr.y}` : `${acc} L ${curr.x} ${curr.y}`;
    }, '');
  };

  // Build active highlighted route path
  let activePathString = '';
  const routeStationIds = new Set<number>();
  if (selectedRoute && selectedRoute.routeSteps && selectedRoute.routeSteps.length > 0) {
    const points = selectedRoute.routeSteps
      .map((step) => {
        routeStationIds.add(step.stationId);
        return stationCoords[step.stationId];
      })
      .filter((pt): pt is { x: number; y: number } => pt !== undefined);

    if (points.length > 0) {
      activePathString = points.reduce((acc, curr, idx) => {
        return idx === 0 ? `M ${curr.x} ${curr.y}` : `${acc} L ${curr.x} ${curr.y}`;
      }, '');
    }
  }

  const sourceStationName = selectedRoute?.source;
  const destStationName = selectedRoute?.destination;

  return (
    <div className="bg-gray-900/90 border border-gray-800 rounded-2xl p-6 shadow-xl space-y-4 backdrop-blur-md">
      {/* Title & Legend Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-3 border-b border-gray-800">
        <div>
          <h2 className="text-lg font-bold text-white tracking-tight flex items-center gap-2">
            <Map className="w-5 h-5 text-blue-400" />
            <span>Interactive Metro Transit Schematic</span>
          </h2>
          <p className="text-xs text-gray-400">
            Click any station node to select as route endpoint • Realtime Dijkstra/BFS path highlight
          </p>
        </div>

        {/* Legend */}
        <div className="flex flex-wrap items-center gap-3 text-xs">
          <div className="flex items-center gap-1.5">
            <span className="w-3 h-1.5 rounded-full bg-blue-500" />
            <span className="text-gray-300">Blue Line</span>
          </div>
          <div className="flex items-center gap-1.5">
            <span className="w-3 h-1.5 rounded-full bg-emerald-500" />
            <span className="text-gray-300">Green Line</span>
          </div>
          <div className="flex items-center gap-1.5">
            <span className="w-3 h-1.5 rounded-full bg-purple-500" />
            <span className="text-gray-300">Purple Line</span>
          </div>
          <div className="flex items-center gap-1.5">
            <svg width="16" height="6"><line x1="0" y1="3" x2="16" y2="3" stroke="#f97316" strokeWidth="2.5" strokeDasharray="4 3"/></svg>
            <span className="text-orange-300">Orange Express</span>
          </div>
          <div className="flex items-center gap-1.5">
            <span className="w-2.5 h-2.5 rounded-full bg-amber-400 border border-white" />
            <span className="text-gray-300">Interchange Hub</span>
          </div>
        </div>
      </div>

      {/* SVG Canvas Container */}
      <div className="relative w-full overflow-hidden rounded-xl border border-gray-800/80 bg-gray-950/80 p-2">
        {/* Zoom Controls */}
        <div className="absolute top-4 right-4 z-10 flex flex-col gap-1.5 bg-gray-900/90 border border-gray-800 rounded-lg p-1 shadow-lg">
          <button
            onClick={() => setZoom((z) => Math.min(z + 0.2, 1.8))}
            title="Zoom In"
            className="p-1.5 text-gray-300 hover:text-white hover:bg-gray-800 rounded transition-colors"
          >
            <ZoomIn className="w-4 h-4" />
          </button>
          <button
            onClick={() => setZoom((z) => Math.max(z - 0.2, 0.8))}
            title="Zoom Out"
            className="p-1.5 text-gray-300 hover:text-white hover:bg-gray-800 rounded transition-colors"
          >
            <ZoomOut className="w-4 h-4" />
          </button>
          <button
            onClick={() => setZoom(1)}
            title="Reset Zoom"
            className="p-1.5 text-gray-300 hover:text-white hover:bg-gray-800 rounded transition-colors"
          >
            <RotateCcw className="w-3.5 h-3.5" />
          </button>
        </div>

        {/* Hover Information Tooltip */}
        {hoveredStation && (
          <div className="absolute bottom-4 left-4 z-10 bg-gray-900/95 border border-gray-700/80 rounded-xl p-3 shadow-xl pointer-events-none text-xs backdrop-blur-md">
            <div className="font-bold text-white text-sm">{hoveredStation.name}</div>
            <div className="text-gray-400 mt-0.5">Line: {hoveredStation.line}</div>
            {hoveredStation.interchange && (
              <div className="text-amber-400 font-semibold mt-1">⇄ Interchange Station</div>
            )}
            <div className="text-blue-400/80 mt-1 font-mono text-[10px]">Click node to select station</div>
          </div>
        )}

        <div className="w-full overflow-auto">
          <svg
            viewBox="0 0 880 640"
            className="w-full h-auto min-w-[700px] transition-transform duration-300"
            style={{ transform: `scale(${zoom})`, transformOrigin: 'center center' }}
          >
            {/* Background Grid Pattern */}
            <defs>
              <pattern id="grid" width="40" height="40" patternUnits="userSpaceOnUse">
                <path d="M 40 0 L 0 0 0 40" fill="none" stroke="rgba(255, 255, 255, 0.03)" strokeWidth="1" />
              </pattern>
            </defs>
            <rect width="100%" height="100%" fill="url(#grid)" />

            {/* Base Metro Track Lines */}
            {/* Green Line */}
            <path
              d={buildPathString(greenLineIds)}
              fill="none"
              stroke="#10B981"
              strokeWidth="6"
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeOpacity="0.45"
            />
            {/* Purple Line */}
            <path
              d={buildPathString(purpleLineIds)}
              fill="none"
              stroke="#A855F7"
              strokeWidth="6"
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeOpacity="0.45"
            />
            {/* Blue Line */}
            <path
              d={buildPathString(blueLineIds)}
              fill="none"
              stroke="#3B82F6"
              strokeWidth="6"
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeOpacity="0.45"
            />

            {/* Orange Line Express — two separate dashed segments */}
            {/* Egmore (16) → Guindy (12) */}
            <path
              d={buildPathString([16, 12])}
              fill="none"
              stroke="#F97316"
              strokeWidth="4"
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeOpacity="0.80"
              strokeDasharray="10 6"
            />
            {/* Vadapalani (27) → Alandur (13) */}
            <path
              d={buildPathString([27, 13])}
              fill="none"
              stroke="#F97316"
              strokeWidth="4"
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeOpacity="0.80"
              strokeDasharray="10 6"
            />

            {/* Selected Active Route (Highlighted & Glowing) */}
            {activePathString && (
              <>
                {/* Glow underlay */}
                <path
                  d={activePathString}
                  fill="none"
                  stroke="#38BDF8"
                  strokeWidth="14"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeOpacity="0.3"
                  className="route-active-pulse"
                />
                {/* Core bright path */}
                <path
                  d={activePathString}
                  fill="none"
                  stroke="#FACC15"
                  strokeWidth="6"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                />
              </>
            )}

            {/* Station Nodes */}
            {stations.map((st) => {
              const coords = stationCoords[st.id];
              if (!coords) return null;

              const isSource = st.name === sourceStationName;
              const isDestination = st.name === destStationName;
              const isInRoute = routeStationIds.has(st.id);
              const isInterchange = st.interchange;

              return (
                <g
                  key={st.id}
                  className="cursor-pointer transition-all duration-200"
                  onMouseEnter={() => setHoveredStation(st)}
                  onMouseLeave={() => setHoveredStation(null)}
                  onClick={() => onSelectStation(st.name)}
                >
                  {/* Outer ring for Source / Destination */}
                  {isSource && (
                    <circle
                      cx={coords.x}
                      cy={coords.y}
                      r="16"
                      fill="none"
                      stroke="#10B981"
                      strokeWidth="3"
                      className="animate-ping origin-center"
                      style={{ transformOrigin: `${coords.x}px ${coords.y}px` }}
                    />
                  )}
                  {isDestination && (
                    <circle
                      cx={coords.x}
                      cy={coords.y}
                      r="16"
                      fill="none"
                      stroke="#EF4444"
                      strokeWidth="3"
                      className="animate-ping origin-center"
                      style={{ transformOrigin: `${coords.x}px ${coords.y}px` }}
                    />
                  )}

                  {/* Station Node Circle */}
                  <circle
                    cx={coords.x}
                    cy={coords.y}
                    r={isSource || isDestination ? 9 : isInterchange ? 7 : isInRoute ? 6 : 5}
                    fill={
                      isSource
                        ? '#10B981'
                        : isDestination
                        ? '#EF4444'
                        : isInterchange
                        ? '#FBBF24'
                        : isInRoute
                        ? '#FACC15'
                        : '#1F2937'
                    }
                    stroke={
                      isSource || isDestination
                        ? '#FFFFFF'
                        : isInterchange
                        ? '#FFFFFF'
                        : isInRoute
                        ? '#000000'
                        : '#9CA3AF'
                    }
                    strokeWidth={isInterchange || isSource || isDestination ? 2.5 : 1.5}
                  />

                  {/* Station Label */}
                  <text
                    x={coords.x + 9}
                    y={coords.y + 3.5}
                    fill={isSource || isDestination ? '#FFFFFF' : isInRoute ? '#F3F4F6' : '#9CA3AF'}
                    fontSize={isSource || isDestination ? '11px' : isInterchange ? '10px' : '9px'}
                    fontWeight={isSource || isDestination || isInterchange ? 'bold' : 'normal'}
                    className="select-none pointer-events-none drop-shadow-md"
                  >
                    {st.name}
                  </text>
                </g>
              );
            })}
          </svg>
        </div>
      </div>
    </div>
  );
};
