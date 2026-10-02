import React, { useEffect, useRef } from 'react';
import {
  type VisualizationResult,
  type AlgorithmEvent,
} from '../services/api';
import {
  Play,
  Pause,
  SkipForward,
  SkipBack,
  RotateCcw,
  Sparkles,
  Maximize2,
  Minimize2,
  X,
  CheckCircle2,
  ArrowRight,
  ListOrdered,
  GitBranch,
  Milestone,
} from 'lucide-react';

interface AlgorithmVisualizerProps {
  visualization: VisualizationResult;
  currentStepIndex: number;
  isPlaying: boolean;
  playbackSpeed: number; // in milliseconds
  presentationMode: boolean;
  onStepChange: (index: number) => void;
  onTogglePlay: () => void;
  onSpeedChange: (speed: number) => void;
  onTogglePresentationMode: () => void;
  onRestart: () => void;
  onClose: () => void;
}

export const AlgorithmVisualizer: React.FC<AlgorithmVisualizerProps> = ({
  visualization,
  currentStepIndex,
  isPlaying,
  playbackSpeed,
  presentationMode,
  onStepChange,
  onTogglePlay,
  onSpeedChange,
  onTogglePresentationMode,
  onRestart,
  onClose,
}) => {
  const events = visualization.events || [];
  const currentEvent: AlgorithmEvent | undefined = events[currentStepIndex];
  const totalSteps = events.length;

  // Auto-play timer
  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null);

  useEffect(() => {
    if (isPlaying) {
      timerRef.current = setInterval(() => {
        if (currentStepIndex < totalSteps - 1) {
          onStepChange(currentStepIndex + 1);
        } else {
          onTogglePlay(); // stop when reached the end
        }
      }, playbackSpeed);
    }

    return () => {
      if (timerRef.current) {
        clearInterval(timerRef.current);
      }
    };
  }, [isPlaying, currentStepIndex, totalSteps, playbackSpeed, onStepChange, onTogglePlay]);

  if (!currentEvent) {
    return null;
  }

  const isDijkstra = visualization.algorithm.toUpperCase().includes('DIJKSTRA');

  // Helpers for Event Type Badge styling
  const getEventTypeBadge = (type: AlgorithmEvent['type']) => {
    switch (type) {
      case 'START':
        return { label: 'Algorithm Start', bg: 'bg-blue-500/20 text-blue-300 border-blue-500/40' };
      case 'PROCESS_STATION':
        return { label: isDijkstra ? 'Extract-Min (PriorityQueue)' : 'Dequeue (FIFO Queue)', bg: 'bg-indigo-500/20 text-indigo-300 border-indigo-500/40' };
      case 'EXAMINE_NEIGHBOR':
        return { label: 'Examine Neighbor', bg: 'bg-cyan-500/20 text-cyan-300 border-cyan-500/40' };
      case 'RELAX_SUCCESS':
        return { label: 'Edge Relaxed (Shorter Path)', bg: 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40' };
      case 'RELAX_SKIPPED':
        return { label: 'Relaxation Skipped', bg: 'bg-amber-500/20 text-amber-300 border-amber-500/40' };
      case 'ENQUEUE':
        return { label: 'Enqueue Unvisited Neighbor', bg: 'bg-purple-500/20 text-purple-300 border-purple-500/40' };
      case 'ALREADY_VISITED':
        return { label: 'Already Visited (Skipped)', bg: 'bg-gray-500/20 text-gray-400 border-gray-500/40' };
      case 'DESTINATION_REACHED':
        return { label: 'Destination Reached!', bg: 'bg-rose-500/20 text-rose-300 border-rose-500/40' };
      case 'BACKTRACK_STEP':
        return { label: 'Parent Backtracking', bg: 'bg-fuchsia-500/20 text-fuchsia-300 border-fuchsia-500/40' };
      case 'FINAL_PATH_READY':
        return { label: 'Final Route Reconstructed', bg: 'bg-amber-400/20 text-amber-300 border-amber-400/50' };
      default:
        return { label: type, bg: 'bg-gray-700 text-gray-300 border-gray-600' };
    }
  };

  const badge = getEventTypeBadge(currentEvent.type);

  return (
    <div
      className={`bg-gray-900/95 border rounded-2xl shadow-2xl backdrop-blur-xl transition-all duration-300 ${
        presentationMode
          ? 'border-blue-500/50 p-6 md:p-8 bg-[#0a0f1d]/98 ring-2 ring-blue-500/30'
          : 'border-gray-800 p-5 md:p-6 space-y-5'
      }`}
    >
      {/* Top Header: Algorithm details & Presentation Toggle */}
      <div className="flex flex-wrap items-center justify-between gap-3 pb-4 border-b border-gray-800">
        <div className="space-y-1">
          <div className="flex items-center gap-2 flex-wrap">
            <span className="text-xs font-bold uppercase tracking-wider px-2.5 py-0.5 rounded-md bg-blue-600 text-white shadow-sm">
              Live Algorithm Execution
            </span>
            <span className="text-xs font-semibold px-2.5 py-0.5 rounded-md bg-gray-800 text-blue-400 border border-gray-700">
              {visualization.algorithm}
            </span>
            <span className="text-xs font-medium px-2.5 py-0.5 rounded-md bg-gray-800 text-gray-300 border border-gray-700">
              Optimization: <strong className="text-white">{visualization.optimization}</strong>
            </span>
            <span className="text-xs text-gray-400 font-mono">
              (Weight: {visualization.weightUnit})
            </span>
          </div>

          <div className="text-base font-bold text-white flex items-center gap-2 pt-1">
            <span>{visualization.source}</span>
            <ArrowRight className="w-4 h-4 text-blue-400" />
            <span>{visualization.destination}</span>
          </div>
        </div>

        {/* Right Controls: Presentation Mode & Close */}
        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={onTogglePresentationMode}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-semibold border transition-all cursor-pointer ${
              presentationMode
                ? 'bg-blue-600 text-white border-blue-400 shadow-lg shadow-blue-500/30'
                : 'bg-gray-800 hover:bg-gray-700 text-gray-300 border-gray-700'
            }`}
            title="Toggle high-visibility presentation mode for viva / demo"
          >
            {presentationMode ? (
              <>
                <Minimize2 className="w-3.5 h-3.5" />
                <span>Exit Presentation</span>
              </>
            ) : (
              <>
                <Maximize2 className="w-3.5 h-3.5" />
                <span>Presentation Mode</span>
              </>
            )}
          </button>

          <button
            type="button"
            onClick={onClose}
            className="p-1.5 rounded-xl bg-gray-800 hover:bg-rose-900/60 text-gray-400 hover:text-rose-200 border border-gray-700 transition-colors cursor-pointer"
            title="Close Visualization"
          >
            <X className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Primary Operation Banner - Designed for Viva Demonstration */}
      <div
        className={`p-4 rounded-xl border transition-all ${
          presentationMode
            ? 'bg-gradient-to-r from-blue-950/70 via-indigo-950/60 to-purple-950/70 border-blue-500/50 shadow-xl'
            : 'bg-gray-950/70 border-gray-800 shadow-md'
        }`}
      >
        <div className="flex flex-wrap items-center justify-between gap-2 mb-2">
          <div className="flex items-center gap-2">
            <span
              className={`text-xs font-bold uppercase tracking-wider px-2.5 py-1 rounded-lg border ${badge.bg}`}
            >
              {badge.label}
            </span>
            <span className="text-xs font-mono text-gray-400 bg-gray-900/90 px-2 py-0.5 rounded border border-gray-800">
              Step {currentStepIndex + 1} of {totalSteps}
            </span>
          </div>

          {currentEvent.edgeLine && (
            <span className="text-xs text-gray-300 px-2.5 py-0.5 rounded bg-gray-800 border border-gray-700 font-medium">
              Track: {currentEvent.edgeLine}
            </span>
          )}
        </div>

        {/* Big readable operation text for projector/screen */}
        <p
          className={`font-medium text-gray-100 leading-relaxed ${
            presentationMode ? 'text-lg sm:text-xl font-semibold text-white' : 'text-sm sm:text-base'
          }`}
        >
          {currentEvent.description}
        </p>
      </div>

      {/* Step Progress Scrubber Bar */}
      <div className="space-y-1.5">
        <div className="flex justify-between text-xs text-gray-400">
          <span>Progress: Step {currentStepIndex + 1} / {totalSteps}</span>
          <span>{Math.round(((currentStepIndex + 1) / totalSteps) * 100)}% Complete</span>
        </div>
        <input
          type="range"
          min={0}
          max={totalSteps - 1}
          value={currentStepIndex}
          onChange={(e) => onStepChange(Number(e.target.value))}
          className="w-full h-2 bg-gray-800 rounded-lg appearance-none cursor-pointer accent-blue-500 focus:outline-none"
        />
      </div>

      {/* Playback Controls Bar */}
      <div className="flex flex-wrap items-center justify-between gap-4 p-3 bg-gray-950/80 rounded-xl border border-gray-800">
        {/* Left: Step Stepping & Play/Pause */}
        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={onRestart}
            title="Restart from Step 1"
            className="p-2 rounded-xl bg-gray-800 hover:bg-gray-700 text-gray-300 hover:text-white border border-gray-700 transition-colors cursor-pointer"
          >
            <RotateCcw className="w-4 h-4" />
          </button>

          <button
            type="button"
            onClick={() => onStepChange(Math.max(0, currentStepIndex - 1))}
            disabled={currentStepIndex === 0}
            title="Previous Step"
            className="p-2 rounded-xl bg-gray-800 hover:bg-gray-700 text-gray-300 hover:text-white border border-gray-700 disabled:opacity-40 disabled:cursor-not-allowed transition-colors cursor-pointer"
          >
            <SkipBack className="w-4 h-4" />
          </button>

          {/* Primary Play / Pause button */}
          <button
            type="button"
            onClick={onTogglePlay}
            className={`flex items-center gap-2 px-4 py-2 rounded-xl font-bold text-sm shadow-md transition-all cursor-pointer ${
              isPlaying
                ? 'bg-amber-600 hover:bg-amber-500 text-white shadow-amber-600/20'
                : 'bg-blue-600 hover:bg-blue-500 text-white shadow-blue-600/20'
            }`}
          >
            {isPlaying ? (
              <>
                <Pause className="w-4 h-4 fill-current" />
                <span>Pause</span>
              </>
            ) : (
              <>
                <Play className="w-4 h-4 fill-current" />
                <span>{currentStepIndex >= totalSteps - 1 ? 'Replay' : 'Play'}</span>
              </>
            )}
          </button>

          {/* NEXT STEP BUTTON (Highlighted for Viva presentation) */}
          <button
            type="button"
            onClick={() => onStepChange(Math.min(totalSteps - 1, currentStepIndex + 1))}
            disabled={currentStepIndex >= totalSteps - 1}
            className="flex items-center gap-1.5 px-3.5 py-2 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-semibold text-sm border border-emerald-500 shadow-md shadow-emerald-600/20 disabled:opacity-40 disabled:cursor-not-allowed transition-all cursor-pointer"
            title="Step Forward (Viva Manual Stepping)"
          >
            <span>Next Step</span>
            <SkipForward className="w-4 h-4" />
          </button>
        </div>

        {/* Right: Speed selector */}
        <div className="flex items-center gap-2 text-xs">
          <span className="text-gray-400 font-medium">Speed:</span>
          <div className="flex items-center bg-gray-900 rounded-lg p-1 border border-gray-800">
            {[
              { label: '0.5x', ms: 1400 },
              { label: '1x', ms: 800 },
              { label: '2x', ms: 400 },
              { label: '4x', ms: 180 },
            ].map((s) => (
              <button
                key={s.label}
                type="button"
                onClick={() => onSpeedChange(s.ms)}
                className={`px-2.5 py-1 rounded-md font-mono text-xs transition-colors cursor-pointer ${
                  playbackSpeed === s.ms
                    ? 'bg-blue-600 text-white font-bold'
                    : 'text-gray-400 hover:text-white'
                }`}
              >
                {s.label}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* Live DSA State Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
        {/* Card 1: Current Station Focus */}
        <div className="bg-gray-950/70 border border-gray-800/80 rounded-xl p-3.5 space-y-2">
          <div className="flex items-center justify-between text-xs text-gray-400">
            <span className="font-semibold uppercase tracking-wider flex items-center gap-1.5 text-blue-400">
              <Milestone className="w-3.5 h-3.5" />
              Current Station
            </span>
            <span className="font-mono text-gray-300">
              {isDijkstra ? `Cost: ${currentEvent.currentCost} ${visualization.weightUnit}` : `Level: ${currentEvent.currentLevel}`}
            </span>
          </div>
          <div className="text-lg font-bold text-white tracking-tight">
            {currentEvent.currentStationName}
          </div>
          {currentEvent.neighborStationName && (
            <div className="text-xs text-cyan-300/90 pt-1 border-t border-gray-800/60 flex items-center gap-1.5">
              <span>Evaluating Edge:</span>
              <strong className="text-white">➔ {currentEvent.neighborStationName}</strong>
              {currentEvent.edgeWeight !== undefined && (
                <span className="text-gray-400">({currentEvent.edgeWeight} {visualization.weightUnit})</span>
              )}
            </div>
          )}
        </div>

        {/* Card 2: Algorithm Specific Metric (Relaxation for Dijkstra, Level for BFS) */}
        {isDijkstra ? (
          <div className="bg-gray-950/70 border border-gray-800/80 rounded-xl p-3.5 space-y-2">
            <div className="flex items-center justify-between text-xs text-gray-400">
              <span className="font-semibold uppercase tracking-wider flex items-center gap-1.5 text-emerald-400">
                <Sparkles className="w-3.5 h-3.5" />
                Edge Relaxation
              </span>
              <span
                className={`text-[10px] font-bold px-2 py-0.5 rounded ${
                  currentEvent.relaxed
                    ? 'bg-emerald-500/20 text-emerald-300 border border-emerald-500/30'
                    : 'bg-gray-800 text-gray-400'
                }`}
              >
                {currentEvent.relaxed ? 'RELAXED' : 'NOT RELAXED'}
              </span>
            </div>
            <div className="grid grid-cols-2 gap-2 text-xs pt-1">
              <div className="bg-gray-900/80 p-2 rounded-lg border border-gray-800">
                <span className="text-gray-500 text-[10px] block">Old Cost</span>
                <span className="font-mono font-semibold text-gray-300">
                  {currentEvent.oldCost !== undefined && currentEvent.oldCost !== null
                    ? `${currentEvent.oldCost} ${visualization.weightUnit}`
                    : '∞ (Unvisited)'}
                </span>
              </div>
              <div className="bg-gray-900/80 p-2 rounded-lg border border-gray-800">
                <span className="text-gray-500 text-[10px] block">New Candidate Cost</span>
                <span className="font-mono font-semibold text-emerald-400">
                  {currentEvent.newCost !== undefined
                    ? `${currentEvent.newCost} ${visualization.weightUnit}`
                    : '—'}
                </span>
              </div>
            </div>
          </div>
        ) : (
          <div className="bg-gray-950/70 border border-gray-800/80 rounded-xl p-3.5 space-y-2">
            <div className="flex items-center justify-between text-xs text-gray-400">
              <span className="font-semibold uppercase tracking-wider flex items-center gap-1.5 text-purple-400">
                <GitBranch className="w-3.5 h-3.5" />
                BFS Level & Depth
              </span>
              <span className="font-mono text-purple-300 font-bold">
                Hop Depth: {currentEvent.currentLevel}
              </span>
            </div>
            <div className="text-xs text-gray-300 pt-1">
              BFS expands level-by-level using FIFO Queue. All neighbors discovered are assigned level <strong className="text-purple-300">{currentEvent.currentLevel + 1}</strong>.
            </div>
          </div>
        )}

        {/* Card 3: Visited Stations Count & Finalized Vertices */}
        <div className="bg-gray-950/70 border border-gray-800/80 rounded-xl p-3.5 space-y-2">
          <div className="flex items-center justify-between text-xs text-gray-400">
            <span className="font-semibold uppercase tracking-wider flex items-center gap-1.5 text-amber-400">
              <CheckCircle2 className="w-3.5 h-3.5" />
              Finalized / Visited Vertices
            </span>
            <span className="font-mono text-amber-300 font-bold">
              {currentEvent.visitedStations?.length || 0} stations
            </span>
          </div>
          <div className="flex flex-wrap gap-1 max-h-16 overflow-y-auto pr-1">
            {(currentEvent.visitedStations || []).slice(-6).map((st) => (
              <span
                key={st}
                className="text-[10px] px-2 py-0.5 rounded bg-gray-900 border border-gray-800 text-gray-300 font-medium"
              >
                {st}
              </span>
            ))}
            {(currentEvent.visitedStations?.length || 0) > 6 && (
              <span className="text-[10px] text-gray-500 self-center">
                +{(currentEvent.visitedStations?.length || 0) - 6} more
              </span>
            )}
          </div>
        </div>
      </div>

      {/* Live Queue / PriorityQueue Inspector */}
      <div className="bg-gray-950/60 border border-gray-800/80 rounded-xl p-4 space-y-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <ListOrdered className="w-4 h-4 text-blue-400" />
            <h4 className="text-xs font-bold text-gray-200 uppercase tracking-wider">
              {isDijkstra ? 'Min-Heap PriorityQueue Inspector' : 'FIFO Queue Inspector'}
            </h4>
            <span className="text-xs text-gray-400 font-mono">
              ({currentEvent.queueSnapshot?.length || 0} nodes queued)
            </span>
          </div>
          <span className="text-[11px] text-gray-500 font-mono">
            {isDijkstra ? 'Greedy extract-min order' : 'FIFO First-In-First-Out order'}
          </span>
        </div>

        {currentEvent.queueSnapshot && currentEvent.queueSnapshot.length > 0 ? (
          <div className="flex flex-wrap items-center gap-2 max-h-24 overflow-y-auto p-1">
            {currentEvent.queueSnapshot.map((item, idx) => (
              <div
                key={`${item.stationId}-${idx}`}
                className={`flex items-center gap-1.5 px-2.5 py-1 rounded-lg border text-xs ${
                  idx === 0
                    ? 'bg-blue-500/15 border-blue-500/40 text-blue-200 font-semibold ring-1 ring-blue-500/30'
                    : 'bg-gray-900 border-gray-800 text-gray-300'
                }`}
              >
                {idx === 0 && (
                  <span className="text-[9px] uppercase font-bold bg-blue-500 text-white px-1 rounded">
                    Next
                  </span>
                )}
                <span>{item.stationName}</span>
                <span className="font-mono text-gray-400 text-[11px]">
                  ({item.cost} {visualization.weightUnit})
                </span>
              </div>
            ))}
          </div>
        ) : (
          <div className="text-xs text-gray-500 italic py-1">
            Queue is currently empty (or finalizing current node).
          </div>
        )}
      </div>

      {/* Backtracking & Path Reconstruction Visualizer (Shown during Backtracking or Final Path) */}
      {(currentEvent.type === 'BACKTRACK_STEP' || currentEvent.type === 'FINAL_PATH_READY') && (
        <div className="p-4 rounded-xl bg-fuchsia-950/20 border border-fuchsia-500/40 space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-fuchsia-300 uppercase tracking-wider flex items-center gap-1.5">
              <RotateCcw className="w-3.5 h-3.5" />
              Parent Array Backtracking ({currentEvent.type === 'FINAL_PATH_READY' ? 'Reversed Forward Path' : 'Destination ➔ Parent ➔ Source'})
            </span>
          </div>

          <div className="flex flex-wrap items-center gap-1.5 pt-1 text-xs">
            {(currentEvent.currentPathSnapshot || []).map((st, idx) => (
              <React.Fragment key={`${st}-${idx}`}>
                <span
                  className={`px-2.5 py-1 rounded-lg border font-semibold ${
                    idx === (currentEvent.currentPathSnapshot?.length || 0) - 1
                      ? 'bg-amber-400 text-gray-950 border-amber-300'
                      : 'bg-gray-900 text-gray-200 border-gray-700'
                  }`}
                >
                  {st}
                </span>
                {idx < (currentEvent.currentPathSnapshot?.length || 0) - 1 && (
                  <span className="text-fuchsia-400 font-bold">➔</span>
                )}
              </React.Fragment>
            ))}
          </div>
        </div>
      )}

      {/* Algorithmic Complexity Educational Footer */}
      <div className="flex flex-wrap items-center justify-between gap-3 pt-3 border-t border-gray-800 text-xs text-gray-400">
        <div className="flex items-center gap-2">
          <span className="font-semibold text-gray-300">DSA Complexity:</span>
          <span className="font-mono text-blue-400 font-bold">{visualization.timeComplexity} Time</span>
          <span>•</span>
          <span className="font-mono text-purple-400 font-bold">{visualization.spaceComplexity} Space</span>
        </div>
        <div className="text-[11px] text-gray-500">
          V = 35 Stations • E = 38 Connections • Actual in-memory graph execution
        </div>
      </div>
    </div>
  );
};
