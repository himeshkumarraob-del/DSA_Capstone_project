import React, { useState } from 'react';
import type { Station, RouteResult, AlgorithmEvent } from '../services/api';
import { Map, ZoomIn, ZoomOut, RotateCcw } from 'lucide-react';

interface MetroMapProps {
  stations: Station[];
  selectedRoute: RouteResult | null;
  onSelectStation: (stationName: string) => void;
  // Optional Visualization props
  isVisualizing?: boolean;
  currentEvent?: AlgorithmEvent | null;
  weightUnit?: string;
  presentationMode?: boolean;
  finalPath?: string[];
}

export const MetroMap: React.FC<MetroMapProps> = ({
  stations,
  selectedRoute,
  onSelectStation,
  isVisualizing = false,
  currentEvent = null,
  weightUnit = 'min',
  presentationMode = false,
  finalPath = [],
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
  const stationNameToCoords: Record<string, { x: number; y: number }> = {};

  stations.forEach((st) => {
    const coords = getCoordinates(st.latitude, st.longitude);
    stationCoords[st.id] = coords;
    stationNameToCoords[st.name.trim().toLowerCase()] = coords;
  });

  // Track lines definition
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

  // Visualization state lookups
  const visitedNamesSet = new Set<string>(
    (currentEvent?.visitedStations || []).map((s) => s.trim().toLowerCase())
  );

  const queuedMap: Record<string, number> = {};
  if (currentEvent?.queueSnapshot) {
    currentEvent.queueSnapshot.forEach((q) => {
      queuedMap[q.stationName.trim().toLowerCase()] = q.cost;
    });
  }

  const currentStationNameLower = currentEvent?.currentStationName?.trim().toLowerCase();
  const neighborStationNameLower = currentEvent?.neighborStationName?.trim().toLowerCase();

  // Final path / backtracking path set
  const activePathStationNames = isVisualizing
    ? (currentEvent?.type === 'BACKTRACK_STEP' || currentEvent?.type === 'FINAL_PATH_READY')
      ? new Set((currentEvent.currentPathSnapshot || []).map((s) => s.trim().toLowerCase()))
      : new Set<string>()
    : new Set<string>((selectedRoute?.stations || []).map((s) => s.trim().toLowerCase()));

  // Build active highlighted route path
  let activePathString = '';
  const routeStationIds = new Set<number>();

  if (!isVisualizing && selectedRoute && selectedRoute.routeSteps && selectedRoute.routeSteps.length > 0) {
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
  } else if (isVisualizing && (currentEvent?.type === 'BACKTRACK_STEP' || currentEvent?.type === 'FINAL_PATH_READY')) {
    const names = currentEvent.currentPathSnapshot || [];
    const points = names
      .map((name) => stationNameToCoords[name.trim().toLowerCase()])
      .filter((pt): pt is { x: number; y: number } => pt !== undefined);

    if (points.length > 0) {
      activePathString = points.reduce((acc, curr, idx) => {
        return idx === 0 ? `M ${curr.x} ${curr.y}` : `${acc} L ${curr.x} ${curr.y}`;
      }, '');
    }
  }

  // Active edge line coordinates (between current station and evaluated neighbor)
  let activeEdgeCoords: { x1: number; y1: number; x2: number; y2: number } | null = null;
  if (isVisualizing && currentStationNameLower && neighborStationNameLower) {
    const cCoords = stationNameToCoords[currentStationNameLower];
    const nCoords = stationNameToCoords[neighborStationNameLower];
    if (cCoords && nCoords) {
      activeEdgeCoords = { x1: cCoords.x, y1: cCoords.y, x2: nCoords.x, y2: nCoords.y };
    }
  }

  const sourceStationName = isVisualizing ? finalPath[0] : selectedRoute?.source;
  const destStationName = isVisualizing ? finalPath[finalPath.length - 1] : selectedRoute?.destination;

  return (
    <div
      className={`bg-gray-900/90 border rounded-2xl p-6 shadow-xl space-y-4 backdrop-blur-md transition-all ${
        presentationMode ? 'border-blue-500/40 ring-1 ring-blue-500/20' : 'border-gray-800'
      }`}
    >
      {/* Title & Legend Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-3 border-b border-gray-800">
        <div>
          <h2 className="text-lg font-bold text-white tracking-tight flex items-center gap-2">
            <Map className="w-5 h-5 text-blue-400" />
            <span>
              {isVisualizing ? 'Live Graph Algorithm Visualization' : 'Interactive Metro Transit Schematic'}
            </span>
          </h2>
          <p className="text-xs text-gray-400">
            {isVisualizing
              ? 'Realtime state of graph vertices and edges during BFS / Dijkstra execution'
              : 'Click any station node to select as route endpoint • Realtime Dijkstra/BFS path highlight'}
          </p>
        </div>

        {/* Legend */}
        {isVisualizing ? (
          <div className="flex flex-wrap items-center gap-2.5 text-[11px]">
            <div className="flex items-center gap-1">
              <span className="w-3 h-3 rounded-full bg-cyan-400 border border-white animate-pulse" />
              <span className="text-cyan-300 font-semibold">Current Vertex</span>
            </div>
            <div className="flex items-center gap-1">
              <span className="w-2.5 h-2.5 rounded-full bg-amber-400 border border-amber-200" />
              <span className="text-amber-300">Queued / Frontier</span>
            </div>
            <div className="flex items-center gap-1">
              <span className="w-2.5 h-2.5 rounded-full bg-indigo-500 border border-indigo-400" />
              <span className="text-indigo-300">Visited</span>
            </div>
            <div className="flex items-center gap-1">
              <span className="w-2.5 h-2.5 rounded-full bg-gray-700 border border-gray-600" />
              <span className="text-gray-400">Unvisited</span>
            </div>
            <div className="flex items-center gap-1">
              <span className="w-2.5 h-2.5 rounded-full bg-yellow-400 border border-white" />
              <span className="text-yellow-300 font-semibold">Final Path</span>
            </div>
          </div>
        ) : (
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
              <svg width="16" height="6">
                <line x1="0" y1="3" x2="16" y2="3" stroke="#f97316" strokeWidth="2.5" strokeDasharray="4 3" />
              </svg>
              <span className="text-orange-300">Orange Express</span>
            </div>
            <div className="flex items-center gap-1.5">
              <span className="w-2.5 h-2.5 rounded-full bg-amber-400 border border-white" />
              <span className="text-gray-300">Interchange Hub</span>
            </div>
          </div>
        )}
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
            {isVisualizing && (
              <div className="mt-1.5 pt-1 border-t border-gray-800 font-mono text-[11px]">
                {visitedNamesSet.has(hoveredStation.name.toLowerCase()) && (
                  <span className="text-indigo-300">Status: Finalized / Visited</span>
                )}
                {queuedMap[hoveredStation.name.toLowerCase()] !== undefined && (
                  <span className="text-amber-300">
                    Status: In Queue (Cost/Level: {queuedMap[hoveredStation.name.toLowerCase()]} {weightUnit})
                  </span>
                )}
                {!visitedNamesSet.has(hoveredStation.name.toLowerCase()) &&
                  queuedMap[hoveredStation.name.toLowerCase()] === undefined && (
                    <span className="text-gray-500">Status: Unvisited</span>
                  )}
              </div>
            )}
            {!isVisualizing && (
              <div className="text-blue-400/80 mt-1 font-mono text-[10px]">Click node to select station</div>
            )}
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
              strokeWidth={isVisualizing ? '4' : '6'}
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeOpacity={isVisualizing ? '0.25' : '0.45'}
            />
            {/* Purple Line */}
            <path
              d={buildPathString(purpleLineIds)}
              fill="none"
              stroke="#A855F7"
              strokeWidth={isVisualizing ? '4' : '6'}
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeOpacity={isVisualizing ? '0.25' : '0.45'}
            />
            {/* Blue Line */}
            <path
              d={buildPathString(blueLineIds)}
              fill="none"
              stroke="#3B82F6"
              strokeWidth={isVisualizing ? '4' : '6'}
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeOpacity={isVisualizing ? '0.25' : '0.45'}
            />

            {/* Orange Line Express */}
            <path
              d={buildPathString([16, 12])}
              fill="none"
              stroke="#F97316"
              strokeWidth="3.5"
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeOpacity={isVisualizing ? '0.4' : '0.80'}
              strokeDasharray="10 6"
            />
            <path
              d={buildPathString([27, 13])}
              fill="none"
              stroke="#F97316"
              strokeWidth="3.5"
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeOpacity={isVisualizing ? '0.4' : '0.80'}
              strokeDasharray="10 6"
            />

            {/* Active Edge during live relaxation/examination */}
            {activeEdgeCoords && (
              <g>
                <line
                  x1={activeEdgeCoords.x1}
                  y1={activeEdgeCoords.y1}
                  x2={activeEdgeCoords.x2}
                  y2={activeEdgeCoords.y2}
                  stroke="#38BDF8"
                  strokeWidth="10"
                  strokeLinecap="round"
                  strokeOpacity="0.4"
                  className="animate-pulse"
                />
                <line
                  x1={activeEdgeCoords.x1}
                  y1={activeEdgeCoords.y1}
                  x2={activeEdgeCoords.x2}
                  y2={activeEdgeCoords.y2}
                  stroke="#F59E0B"
                  strokeWidth="4"
                  strokeLinecap="round"
                  strokeDasharray="6 4"
                />
              </g>
            )}

            {/* Selected Active Route / Final Path (Highlighted & Glowing) */}
            {activePathString && (
              <>
                <path
                  d={activePathString}
                  fill="none"
                  stroke="#38BDF8"
                  strokeWidth="14"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeOpacity="0.35"
                  className="route-active-pulse"
                />
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

              const stNameLower = st.name.trim().toLowerCase();
              const isSource = st.name === sourceStationName;
              const isDestination = st.name === destStationName;
              const isInterchange = st.interchange;

              // Visualization States
              const isCurrent = isVisualizing && currentStationNameLower === stNameLower;
              const isNeighbor = isVisualizing && neighborStationNameLower === stNameLower;
              const isQueued = isVisualizing && queuedMap[stNameLower] !== undefined;
              const isVisited = isVisualizing && visitedNamesSet.has(stNameLower);
              const isInActivePath = activePathStationNames.has(stNameLower);

              // Determine station node color & radius
              let nodeRadius = isSource || isDestination ? 9 : isInterchange ? 7 : 5;
              let nodeFill = '#1F2937';
              let nodeStroke = '#6B7280';
              let nodeStrokeWidth = 1.5;

              if (isVisualizing) {
                if (isCurrent) {
                  nodeRadius = 10;
                  nodeFill = '#06B6D4';
                  nodeStroke = '#FFFFFF';
                  nodeStrokeWidth = 3;
                } else if (isNeighbor) {
                  nodeRadius = 8;
                  nodeFill = '#F59E0B';
                  nodeStroke = '#FFFFFF';
                  nodeStrokeWidth = 2.5;
                } else if (isInActivePath) {
                  nodeRadius = 8;
                  nodeFill = '#FACC15';
                  nodeStroke = '#000000';
                  nodeStrokeWidth = 2;
                } else if (isSource) {
                  nodeRadius = 9;
                  nodeFill = '#10B981';
                  nodeStroke = '#FFFFFF';
                  nodeStrokeWidth = 2.5;
                } else if (isDestination) {
                  nodeRadius = 9;
                  nodeFill = '#EF4444';
                  nodeStroke = '#FFFFFF';
                  nodeStrokeWidth = 2.5;
                } else if (isQueued) {
                  nodeRadius = 7;
                  nodeFill = '#D97706';
                  nodeStroke = '#FDE68A';
                  nodeStrokeWidth = 2;
                } else if (isVisited) {
                  nodeRadius = 6;
                  nodeFill = '#4F46E5';
                  nodeStroke = '#A5B4FC';
                  nodeStrokeWidth = 1.5;
                } else {
                  // Unvisited
                  nodeRadius = 4.5;
                  nodeFill = '#1E293B';
                  nodeStroke = '#475569';
                  nodeStrokeWidth = 1;
                }
              } else {
                // Standard mode
                if (isSource) {
                  nodeFill = '#10B981';
                  nodeStroke = '#FFFFFF';
                  nodeStrokeWidth = 2.5;
                } else if (isDestination) {
                  nodeFill = '#EF4444';
                  nodeStroke = '#FFFFFF';
                  nodeStrokeWidth = 2.5;
                } else if (isInterchange) {
                  nodeFill = '#FBBF24';
                  nodeStroke = '#FFFFFF';
                  nodeStrokeWidth = 2.5;
                } else if (isInActivePath) {
                  nodeFill = '#FACC15';
                  nodeStroke = '#000000';
                  nodeStrokeWidth = 2;
                } else {
                  nodeFill = '#1F2937';
                  nodeStroke = '#9CA3AF';
                  nodeStrokeWidth = 1.5;
                }
              }

              return (
                <g
                  key={st.id}
                  className="cursor-pointer transition-all duration-200"
                  onMouseEnter={() => setHoveredStation(st)}
                  onMouseLeave={() => setHoveredStation(null)}
                  onClick={() => onSelectStation(st.name)}
                >
                  {/* Outer Pulsing Halo for Current Node during Visualization */}
                  {isCurrent && (
                    <circle
                      cx={coords.x}
                      cy={coords.y}
                      r="18"
                      fill="none"
                      stroke="#06B6D4"
                      strokeWidth="3"
                      className="animate-ping origin-center"
                      style={{ transformOrigin: `${coords.x}px ${coords.y}px` }}
                    />
                  )}

                  {/* Outer Pulsing Halo for Source / Destination */}
                  {!isCurrent && isSource && (
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
                  {!isCurrent && isDestination && (
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
                    r={nodeRadius}
                    fill={nodeFill}
                    stroke={nodeStroke}
                    strokeWidth={nodeStrokeWidth}
                  />

                  {/* Station Label */}
                  <text
                    x={coords.x + 9}
                    y={coords.y + 3.5}
                    fill={
                      isCurrent
                        ? '#38BDF8'
                        : isNeighbor
                        ? '#FBBF24'
                        : isSource || isDestination
                        ? '#FFFFFF'
                        : isInActivePath
                        ? '#F3F4F6'
                        : isQueued
                        ? '#FDE68A'
                        : isVisited
                        ? '#C7D2FE'
                        : '#9CA3AF'
                    }
                    fontSize={
                      isCurrent || presentationMode
                        ? '12px'
                        : isSource || isDestination
                        ? '11px'
                        : isInterchange
                        ? '10px'
                        : '9px'
                    }
                    fontWeight={
                      isCurrent || isSource || isDestination || isInterchange || isInActivePath
                        ? 'bold'
                        : 'normal'
                    }
                    className="select-none pointer-events-none drop-shadow-md"
                  >
                    {st.name}
                    {isVisualizing && isQueued && !isCurrent && (
                      <tspan fill="#F59E0B" fontSize="8px">
                        {' '}[{queuedMap[stNameLower]} {weightUnit}]
                      </tspan>
                    )}
                    {isVisualizing && isCurrent && (
                      <tspan fill="#67E8F9" fontSize="9px">
                        {' '}◀ (Current)
                      </tspan>
                    )}
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
