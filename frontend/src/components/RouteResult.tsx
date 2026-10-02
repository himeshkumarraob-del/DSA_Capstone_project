import React from 'react';
import type { RouteResult as RouteResultType } from '../services/api';
import { Clock, MapPin, Shuffle, Milestone, Compass, ArrowRight, Layers } from 'lucide-react';

interface RouteResultProps {
  result: RouteResultType;
}

export const RouteResult: React.FC<RouteResultProps> = ({ result }) => {
  const getLineColor = (line: string) => {
    if (line.toLowerCase().includes('blue'))   return 'bg-blue-500 text-blue-100 border-blue-400';
    if (line.toLowerCase().includes('green'))  return 'bg-emerald-500 text-emerald-100 border-emerald-400';
    if (line.toLowerCase().includes('purple')) return 'bg-purple-500 text-purple-100 border-purple-400';
    if (line.toLowerCase().includes('orange')) return 'bg-orange-500 text-orange-100 border-orange-400';
    return 'bg-gray-600 text-gray-100 border-gray-500';
  };

  const getLineBadge = (line: string) => {
    if (line.toLowerCase().includes('blue'))   return 'bg-blue-500/15 text-blue-400 border-blue-500/30';
    if (line.toLowerCase().includes('green'))  return 'bg-emerald-500/15 text-emerald-400 border-emerald-500/30';
    if (line.toLowerCase().includes('purple')) return 'bg-purple-500/15 text-purple-400 border-purple-500/30';
    if (line.toLowerCase().includes('orange')) return 'bg-orange-500/15 text-orange-400 border-orange-500/30';
    return 'bg-gray-500/15 text-gray-400 border-gray-500/30';
  };

  const getLineBorder = (line: string) => {
    if (line.toLowerCase().includes('blue'))   return 'border-blue-500';
    if (line.toLowerCase().includes('green'))  return 'border-emerald-500';
    if (line.toLowerCase().includes('purple')) return 'border-purple-500';
    if (line.toLowerCase().includes('orange')) return 'border-orange-500';
    return 'border-gray-500';
  };

  return (
    <div className="bg-gray-900/90 border border-gray-800 rounded-2xl p-6 shadow-xl space-y-6 backdrop-blur-md">
      {/* Header & Algorithm Badge */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-4 border-b border-gray-800 gap-3">
        <div>
          <div className="text-xs uppercase font-semibold text-gray-400 tracking-wider">
            Optimal Journey Calculated
          </div>
          <h2 className="text-xl font-bold text-white tracking-tight flex items-center gap-2">
            <span>{result.source}</span>
            <ArrowRight className="w-4 h-4 text-gray-500" />
            <span>{result.destination}</span>
          </h2>
        </div>

        <div className="flex flex-wrap items-center gap-2">
          <div className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-blue-500/10 border border-blue-500/30 text-blue-400 text-xs font-semibold">
            <Compass className="w-3.5 h-3.5" />
            <span>Algorithm: {result.algorithmUsed}</span>
          </div>
        </div>
      </div>

      {/* Primary Statistics Cards */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
        {/* Stops */}
        <div className="bg-gray-950/70 border border-gray-800/80 rounded-xl p-3.5 flex items-center gap-3">
          <div className="w-10 h-10 rounded-lg bg-indigo-500/10 border border-indigo-500/20 flex items-center justify-center text-indigo-400">
            <Milestone className="w-5 h-5" />
          </div>
          <div>
            <div className="text-[11px] font-medium text-gray-400 uppercase tracking-wider">Stops</div>
            <div className="text-xl font-bold text-white tracking-tight">{result.stops}</div>
          </div>
        </div>

        {/* Time */}
        <div className="bg-gray-950/70 border border-gray-800/80 rounded-xl p-3.5 flex items-center gap-3">
          <div className="w-10 h-10 rounded-lg bg-amber-500/10 border border-amber-500/20 flex items-center justify-center text-amber-400">
            <Clock className="w-5 h-5" />
          </div>
          <div>
            <div className="text-[11px] font-medium text-gray-400 uppercase tracking-wider">Est. Time</div>
            <div className="text-xl font-bold text-white tracking-tight">{result.time} <span className="text-xs text-gray-400 font-normal">min</span></div>
          </div>
        </div>

        {/* Distance */}
        <div className="bg-gray-950/70 border border-gray-800/80 rounded-xl p-3.5 flex items-center gap-3">
          <div className="w-10 h-10 rounded-lg bg-emerald-500/10 border border-emerald-500/20 flex items-center justify-center text-emerald-400">
            <MapPin className="w-5 h-5" />
          </div>
          <div>
            <div className="text-[11px] font-medium text-gray-400 uppercase tracking-wider">Distance</div>
            <div className="text-xl font-bold text-white tracking-tight">{result.distance} <span className="text-xs text-gray-400 font-normal">km</span></div>
          </div>
        </div>

        {/* Interchanges */}
        <div className="bg-gray-950/70 border border-gray-800/80 rounded-xl p-3.5 flex items-center gap-3">
          <div className="w-10 h-10 rounded-lg bg-purple-500/10 border border-purple-500/20 flex items-center justify-center text-purple-400">
            <Shuffle className="w-5 h-5" />
          </div>
          <div>
            <div className="text-[11px] font-medium text-gray-400 uppercase tracking-wider">Interchanges</div>
            <div className="text-xl font-bold text-white tracking-tight">{result.interchanges}</div>
          </div>
        </div>
      </div>

      {/* Metro Lines Traversed */}
      <div className="flex flex-wrap items-center gap-2 p-3 bg-gray-950/50 rounded-xl border border-gray-800/60 text-xs">
        <span className="text-gray-400 flex items-center gap-1.5 font-medium">
          <Layers className="w-3.5 h-3.5 text-gray-400" />
          Lines Traversed:
        </span>
        <div className="flex flex-wrap items-center gap-1.5">
          {result.lines.map((line, idx) => (
            <React.Fragment key={line}>
              <span className={`px-2.5 py-0.5 rounded-full border text-[11px] font-semibold ${getLineBadge(line)}`}>
                {line}
              </span>
              {idx < result.lines.length - 1 && (
                <span className="text-gray-500">➔</span>
              )}
            </React.Fragment>
          ))}
        </div>
      </div>

      {/* Vertical Route Timeline */}
      <div className="pt-2">
        <h3 className="text-sm font-bold text-gray-200 uppercase tracking-wider mb-4 flex items-center gap-2">
          <span>Route Timeline</span>
          <span className="text-xs text-gray-400 font-normal">({result.stations.length} stations in order)</span>
        </h3>

        <div className="relative pl-6 sm:pl-8 space-y-6 before:absolute before:left-[17px] sm:before:left-[21px] before:top-3 before:bottom-3 before:w-0.5 before:bg-gray-700">
          {result.routeSteps.map((step, index) => {
            const isFirst = index === 0;
            const isLast = index === result.routeSteps.length - 1;
            const isTransfer = step.interchange;
            const lineColor = getLineColor(step.line);
            const lineBorder = getLineBorder(step.line);

            return (
              <div key={`${step.stationId}-${index}`} className="relative group">
                {/* Node marker on the vertical track */}
                <div
                  className={`absolute -left-[23px] sm:-left-[27px] top-1.5 flex items-center justify-center transition-transform group-hover:scale-110 ${
                    isFirst || isLast
                      ? 'w-5 h-5 rounded-full ring-4 ring-gray-900 bg-white border-2 ' + lineBorder
                      : isTransfer
                      ? 'w-5 h-5 rounded-full ring-4 ring-gray-900 bg-amber-400 border-2 border-white'
                      : 'w-3.5 h-3.5 rounded-full ml-[3px] ring-2 ring-gray-900 ' + lineColor
                  }`}
                >
                  {(isFirst || isLast) && (
                    <span className="w-2 h-2 rounded-full bg-gray-900" />
                  )}
                </div>

                {/* Station Card Content */}
                <div
                  className={`p-3.5 rounded-xl border transition-all ${
                    isFirst || isLast
                      ? 'bg-gray-800/80 border-gray-700 shadow-md'
                      : isTransfer
                      ? 'bg-amber-950/20 border-amber-500/40 shadow-sm'
                      : 'bg-gray-950/50 border-gray-800/80 hover:border-gray-700'
                  }`}
                >
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-1.5">
                    <div className="flex items-center gap-2">
                      <span className="font-semibold text-white text-sm">
                        {step.stationName}
                      </span>
                      {isFirst && (
                        <span className="text-[10px] uppercase font-bold tracking-wider px-2 py-0.5 rounded bg-emerald-500/20 text-emerald-300 border border-emerald-500/30">
                          Origin
                        </span>
                      )}
                      {isLast && (
                        <span className="text-[10px] uppercase font-bold tracking-wider px-2 py-0.5 rounded bg-rose-500/20 text-rose-300 border border-rose-500/30">
                          Destination
                        </span>
                      )}
                      {isTransfer && (
                        <span className="text-[10px] uppercase font-bold tracking-wider px-2 py-0.5 rounded bg-amber-500/20 text-amber-300 border border-amber-500/30 flex items-center gap-1">
                          <Shuffle className="w-3 h-3" /> Interchange
                        </span>
                      )}
                    </div>

                    <div className="flex items-center gap-3 text-xs text-gray-400">
                      <span className={`px-2 py-0.5 rounded text-[10px] font-semibold border ${getLineBadge(step.line)}`}>
                        {step.line}
                      </span>
                      <span>{step.cumulativeTime} min</span>
                      <span>•</span>
                      <span>{step.cumulativeDistance} km</span>
                    </div>
                  </div>

                  {isTransfer && (
                    <div className="mt-2 text-xs text-amber-200/90 bg-amber-500/10 border border-amber-500/20 rounded-lg p-2 flex items-center gap-2">
                      <Shuffle className="w-3.5 h-3.5 text-amber-400 shrink-0" />
                      <span>Switch trains at <strong>{step.stationName}</strong> to continue onto <strong>{step.line}</strong></span>
                    </div>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};
