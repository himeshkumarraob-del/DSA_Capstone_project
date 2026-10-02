import React from 'react';
import type { Station, RoutePreference } from '../services/api';
import { ArrowUpDown, Zap, Ruler, GitCommit, Search, Sparkles, Eye } from 'lucide-react';

interface RouteSelectorProps {
  stations: Station[];
  source: string;
  destination: string;
  preference: RoutePreference;
  loading: boolean;
  visualizing: boolean;
  onSourceChange: (station: string) => void;
  onDestinationChange: (station: string) => void;
  onPreferenceChange: (pref: RoutePreference) => void;
  onSwapStations: () => void;
  onSubmit: (e: React.FormEvent) => void;
  onStartVisualization: () => void;
  onSelectPreset: (src: string, dest: string, pref: RoutePreference) => void;
}

export const RouteSelector: React.FC<RouteSelectorProps> = ({
  stations,
  source,
  destination,
  preference,
  loading,
  visualizing,
  onSourceChange,
  onDestinationChange,
  onPreferenceChange,
  onSwapStations,
  onSubmit,
  onStartVisualization,
  onSelectPreset,
}) => {
  return (
    <div className="bg-gray-900/90 border border-gray-800 rounded-2xl p-6 shadow-xl relative overflow-hidden backdrop-blur-md">
      {/* Decorative gradient blur background */}
      <div className="absolute top-0 right-0 -mr-16 -mt-16 w-48 h-48 bg-blue-600/10 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-0 left-0 -ml-16 -mb-16 w-48 h-48 bg-purple-600/10 rounded-full blur-3xl pointer-events-none" />

      <div className="relative z-10">
        <div className="flex items-center justify-between mb-5">
          <div>
            <h2 className="text-lg font-bold text-white tracking-tight flex items-center gap-2">
              Find Metro Route
            </h2>
            <p className="text-xs text-gray-400">
              Select origin and destination to compute optimal path via Java DSA
            </p>
          </div>
          <span className="text-xs font-mono text-gray-400 bg-gray-800/80 px-2.5 py-1 rounded-md border border-gray-700/50">
            InMemory Graph
          </span>
        </div>

        <form onSubmit={onSubmit} className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-[1fr,auto,1fr] gap-3 items-center">
            {/* Source Station */}
            <div className="space-y-1.5">
              <label htmlFor="source-station" className="block text-xs font-semibold text-gray-300 uppercase tracking-wider">
                From Station
              </label>
              <div className="relative">
                <select
                  id="source-station"
                  value={source}
                  onChange={(e) => onSourceChange(e.target.value)}
                  className="w-full bg-gray-950 border border-gray-700 text-gray-100 text-sm rounded-xl px-3.5 py-2.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all appearance-none cursor-pointer pr-10"
                >
                  <option value="" disabled>
                    -- Select Source Station --
                  </option>
                  {stations.map((st) => (
                    <option key={`src-${st.id}`} value={st.name}>
                      {st.name} ({st.line}) {st.interchange ? '⇄ Interchange' : ''}
                    </option>
                  ))}
                </select>
                <div className="pointer-events-none absolute inset-y-0 right-0 flex items-center px-3 text-gray-400">
                  <svg className="w-4 h-4 fill-current" viewBox="0 0 20 20">
                    <path d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" />
                  </svg>
                </div>
              </div>
            </div>

            {/* Swap Button */}
            <div className="flex justify-center md:pt-5">
              <button
                type="button"
                onClick={onSwapStations}
                title="Swap Source and Destination"
                className="p-2.5 rounded-xl bg-gray-800 hover:bg-gray-700 text-gray-300 hover:text-white border border-gray-700 hover:border-gray-600 transition-colors shadow-sm cursor-pointer"
              >
                <ArrowUpDown className="w-4 h-4" />
              </button>
            </div>

            {/* Destination Station */}
            <div className="space-y-1.5">
              <label htmlFor="dest-station" className="block text-xs font-semibold text-gray-300 uppercase tracking-wider">
                To Station
              </label>
              <div className="relative">
                <select
                  id="dest-station"
                  value={destination}
                  onChange={(e) => onDestinationChange(e.target.value)}
                  className="w-full bg-gray-950 border border-gray-700 text-gray-100 text-sm rounded-xl px-3.5 py-2.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all appearance-none cursor-pointer pr-10"
                >
                  <option value="" disabled>
                    -- Select Destination Station --
                  </option>
                  {stations.map((st) => (
                    <option key={`dest-${st.id}`} value={st.name}>
                      {st.name} ({st.line}) {st.interchange ? '⇄ Interchange' : ''}
                    </option>
                  ))}
                </select>
                <div className="pointer-events-none absolute inset-y-0 right-0 flex items-center px-3 text-gray-400">
                  <svg className="w-4 h-4 fill-current" viewBox="0 0 20 20">
                    <path d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" />
                  </svg>
                </div>
              </div>
            </div>
          </div>

          {/* Route Preferences Mode */}
          <div className="space-y-2 pt-2">
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider">
              Optimization Algorithm / Preference
            </label>
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-2.5">
              {/* Option 1: Fastest Route (Dijkstra - Time) */}
              <button
                type="button"
                onClick={() => onPreferenceChange('FASTEST')}
                className={`flex items-start gap-3 p-3 rounded-xl border text-left transition-all cursor-pointer ${
                  preference === 'FASTEST'
                    ? 'bg-blue-600/15 border-blue-500 text-white shadow-md shadow-blue-500/10'
                    : 'bg-gray-950/60 border-gray-800 text-gray-400 hover:border-gray-700 hover:text-gray-300'
                }`}
              >
                <div
                  className={`p-2 rounded-lg ${
                    preference === 'FASTEST' ? 'bg-blue-500 text-white' : 'bg-gray-800 text-gray-400'
                  }`}
                >
                  <Zap className="w-4 h-4" />
                </div>
                <div>
                  <div className="text-sm font-semibold">Fastest Route</div>
                  <div className="text-[11px] text-gray-400">Dijkstra (Travel Time)</div>
                </div>
              </button>

              {/* Option 2: Shortest Distance (Dijkstra - Distance) */}
              <button
                type="button"
                onClick={() => onPreferenceChange('SHORTEST_DISTANCE')}
                className={`flex items-start gap-3 p-3 rounded-xl border text-left transition-all cursor-pointer ${
                  preference === 'SHORTEST_DISTANCE'
                    ? 'bg-emerald-600/15 border-emerald-500 text-white shadow-md shadow-emerald-500/10'
                    : 'bg-gray-950/60 border-gray-800 text-gray-400 hover:border-gray-700 hover:text-gray-300'
                }`}
              >
                <div
                  className={`p-2 rounded-lg ${
                    preference === 'SHORTEST_DISTANCE'
                      ? 'bg-emerald-500 text-white'
                      : 'bg-gray-800 text-gray-400'
                  }`}
                >
                  <Ruler className="w-4 h-4" />
                </div>
                <div>
                  <div className="text-sm font-semibold">Shortest Distance</div>
                  <div className="text-[11px] text-gray-400">Dijkstra (Physical Distance)</div>
                </div>
              </button>

              {/* Option 3: Fewest Stops (BFS) */}
              <button
                type="button"
                onClick={() => onPreferenceChange('FEWEST_STOPS')}
                className={`flex items-start gap-3 p-3 rounded-xl border text-left transition-all cursor-pointer ${
                  preference === 'FEWEST_STOPS'
                    ? 'bg-purple-600/15 border-purple-500 text-white shadow-md shadow-purple-500/10'
                    : 'bg-gray-950/60 border-gray-800 text-gray-400 hover:border-gray-700 hover:text-gray-300'
                }`}
              >
                <div
                  className={`p-2 rounded-lg ${
                    preference === 'FEWEST_STOPS'
                      ? 'bg-purple-500 text-white'
                      : 'bg-gray-800 text-gray-400'
                  }`}
                >
                  <GitCommit className="w-4 h-4" />
                </div>
                <div>
                  <div className="text-sm font-semibold">Fewest Stops</div>
                  <div className="text-[11px] text-gray-400">BFS (Level Traversal)</div>
                </div>
              </button>
            </div>
          </div>

          {/* Action Buttons: Find Route AND Visualize Algorithm */}
          <div className="pt-2 grid grid-cols-1 sm:grid-cols-2 gap-3">
            {/* Button 1: Standard Find Route */}
            <button
              type="submit"
              disabled={loading || visualizing}
              className="w-full bg-gradient-to-r from-blue-600 via-indigo-600 to-blue-700 hover:from-blue-500 hover:to-indigo-600 text-white font-semibold py-3 px-4 rounded-xl shadow-lg shadow-blue-600/25 transition-all flex items-center justify-center gap-2 cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed text-xs sm:text-sm uppercase tracking-wider"
            >
              {loading ? (
                <>
                  <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  <span>Computing...</span>
                </>
              ) : (
                <>
                  <Search className="w-4 h-4" />
                  <span>Find Route</span>
                </>
              )}
            </button>

            {/* Button 2: Visualize Algorithm (Step-by-Step Educational Explorer) */}
            <button
              type="button"
              onClick={onStartVisualization}
              disabled={loading || visualizing}
              className="w-full bg-gradient-to-r from-amber-600 via-orange-600 to-amber-700 hover:from-amber-500 hover:to-orange-600 text-white font-semibold py-3 px-4 rounded-xl shadow-lg shadow-orange-600/25 transition-all flex items-center justify-center gap-2 cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed text-xs sm:text-sm uppercase tracking-wider"
            >
              {visualizing ? (
                <>
                  <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  <span>Initializing DSA...</span>
                </>
              ) : (
                <>
                  <Eye className="w-4 h-4" />
                  <span>Visualize Algorithm</span>
                </>
              )}
            </button>
          </div>
        </form>

        {/* Quick Demo Presets */}
        <div className="mt-5 pt-4 border-t border-gray-800/80">
          <div className="flex items-center gap-1.5 text-xs text-gray-400 mb-2.5">
            <Sparkles className="w-3.5 h-3.5 text-amber-400" />
            <span className="font-semibold text-gray-300">Quick Test Routes (Viva Demo):</span>
          </div>
          <div className="flex flex-wrap gap-2">
            <button
              type="button"
              onClick={() => onSelectPreset('Chennai Central', 'Chennai International Airport', 'FASTEST')}
              className="text-xs bg-gray-800/80 hover:bg-gray-700 text-gray-200 px-3 py-1.5 rounded-lg border border-gray-700/60 transition-colors cursor-pointer"
            >
              Case 1: Central ➔ Airport (Fastest)
            </button>
            <button
              type="button"
              onClick={() => onSelectPreset('Chennai Central', 'Chennai International Airport', 'SHORTEST_DISTANCE')}
              className="text-xs bg-gray-800/80 hover:bg-gray-700 text-gray-200 px-3 py-1.5 rounded-lg border border-gray-700/60 transition-colors cursor-pointer"
            >
              Case 1: Central ➔ Airport (Shortest)
            </button>
            <button
              type="button"
              onClick={() => onSelectPreset('Koyambedu', 'Chennai International Airport', 'FASTEST')}
              className="text-xs bg-gray-800/80 hover:bg-gray-700 text-gray-200 px-3 py-1.5 rounded-lg border border-gray-700/60 transition-colors cursor-pointer"
            >
              Case 2: Koyambedu ➔ Airport (Interchange)
            </button>
            <button
              type="button"
              onClick={() => onSelectPreset('Madhavaram', 'Adyar Depot', 'FEWEST_STOPS')}
              className="text-xs bg-gray-800/80 hover:bg-gray-700 text-gray-200 px-3 py-1.5 rounded-lg border border-gray-700/60 transition-colors cursor-pointer"
            >
              Madhavaram ➔ Adyar (BFS Fewest Stops)
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
