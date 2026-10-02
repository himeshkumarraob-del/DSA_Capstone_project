import React, { useState, useEffect } from 'react';
import { Header } from './components/Header';
import { RouteSelector } from './components/RouteSelector';
import { RouteResult } from './components/RouteResult';
import { MetroMap } from './components/MetroMap';
import { AlgorithmVisualizer } from './components/AlgorithmVisualizer';
import { AlgorithmInfo } from './components/AlgorithmInfo';
import {
  type Station,
  type RouteResult as RouteResultType,
  type RoutePreference,
  type VisualizationResult,
  fetchStations,
  findRoute,
  visualizeRoute,
  checkBackendHealth,
} from './services/api';
import { AlertTriangle, RefreshCw } from 'lucide-react';

export const App: React.FC = () => {
  const [stations, setStations] = useState<Station[]>([]);
  const [source, setSource] = useState<string>('');
  const [destination, setDestination] = useState<string>('');
  const [preference, setPreference] = useState<RoutePreference>('FASTEST');
  const [routeResult, setRouteResult] = useState<RouteResultType | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [visualizing, setVisualizing] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [backendOnline, setBackendOnline] = useState<boolean>(false);
  const [initialLoading, setInitialLoading] = useState<boolean>(true);

  // Visualization State
  const [visualization, setVisualization] = useState<VisualizationResult | null>(null);
  const [currentStepIndex, setCurrentStepIndex] = useState<number>(0);
  const [isPlaying, setIsPlaying] = useState<boolean>(false);
  const [playbackSpeed, setPlaybackSpeed] = useState<number>(800); // 800ms
  const [presentationMode, setPresentationMode] = useState<boolean>(false);

  // Initialize and load station dataset
  useEffect(() => {
    const initializeApp = async () => {
      setInitialLoading(true);
      setError(null);

      const isHealthy = await checkBackendHealth();
      setBackendOnline(isHealthy);

      if (!isHealthy) {
        setError(
          'Unable to connect to MetroPath backend. Please make sure the Java server is running on http://localhost:8080.'
        );
        setInitialLoading(false);
        return;
      }

      try {
        const stationList = await fetchStations();
        setStations(stationList);

        // Pre-select popular route for immediate demonstration
        const defaultSource = 'Chennai Central';
        const defaultDest = 'Chennai International Airport';
        setSource(defaultSource);
        setDestination(defaultDest);

        // Automatically compute initial route for instant visual satisfaction
        try {
          const initialRoute = await findRoute(defaultSource, defaultDest, 'FASTEST');
          setRouteResult(initialRoute);
        } catch {
          // If default fails, user can still pick manually
        }
      } catch (err: any) {
        setError(err.message || 'Failed to load station list from backend.');
      } finally {
        setInitialLoading(false);
      }
    };

    initializeApp();

    // Periodic health check
    const interval = setInterval(async () => {
      const healthy = await checkBackendHealth();
      setBackendOnline(healthy);
    }, 15000);

    return () => clearInterval(interval);
  }, []);

  // Validation helper
  const validateEndpoints = () => {
    if (!source) {
      setError('Please select a starting station.');
      return false;
    }
    if (!destination) {
      setError('Please select a destination station.');
      return false;
    }
    if (source.trim().toLowerCase() === destination.trim().toLowerCase()) {
      setError('Source and destination must be different.');
      return false;
    }
    return true;
  };

  // Handle standard route submission
  const handleCalculateRoute = async (e?: React.FormEvent) => {
    if (e) e.preventDefault();

    if (!validateEndpoints()) return;

    setLoading(true);
    setError(null);
    setVisualization(null); // close visualization if standard route requested
    setIsPlaying(false);

    try {
      const result = await findRoute(source, destination, preference);
      setRouteResult(result);
    } catch (err: any) {
      setError(err.message || 'No route could be found between these stations.');
      setRouteResult(null);
    } finally {
      setLoading(false);
    }
  };

  // Handle step-by-step algorithm visualization
  const handleStartVisualization = async () => {
    if (!validateEndpoints()) return;

    setVisualizing(true);
    setError(null);
    setIsPlaying(false);

    try {
      const visResult = await visualizeRoute(source, destination, preference);
      setVisualization(visResult);
      setRouteResult(visResult.routeResult);
      setCurrentStepIndex(0);
      setIsPlaying(true); // auto-play upon launch
    } catch (err: any) {
      setError(err.message || 'Failed to generate algorithm visualization.');
      setVisualization(null);
    } finally {
      setVisualizing(false);
    }
  };

  const handleSwapStations = () => {
    const temp = source;
    setSource(destination);
    setDestination(temp);
    setVisualization(null);
    setIsPlaying(false);
  };

  const handleSelectPreset = (src: string, dest: string, pref: RoutePreference) => {
    setSource(src);
    setDestination(dest);
    setPreference(pref);
    setError(null);
    setLoading(true);
    setVisualization(null);
    setIsPlaying(false);

    findRoute(src, dest, pref)
      .then((res) => setRouteResult(res))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  };

  // Node click in MetroMap: set source or destination
  const handleMapStationSelect = (stationName: string) => {
    if (!source || (source && destination)) {
      setSource(stationName);
      setDestination('');
      setError(null);
    } else if (source && !destination) {
      if (source === stationName) {
        setError('Source and destination must be different.');
        return;
      }
      setDestination(stationName);
      setError(null);
    }
  };

  // Visualizer controls
  const handleStepChange = (index: number) => {
    setCurrentStepIndex(index);
  };

  const handleTogglePlay = () => {
    if (!visualization) return;
    if (currentStepIndex >= visualization.events.length - 1) {
      setCurrentStepIndex(0);
      setIsPlaying(true);
    } else {
      setIsPlaying((prev) => !prev);
    }
  };

  const handleSpeedChange = (speed: number) => {
    setPlaybackSpeed(speed);
  };

  const handleTogglePresentationMode = () => {
    setPresentationMode((prev) => !prev);
  };

  const handleRestartVisualization = () => {
    setCurrentStepIndex(0);
    setIsPlaying(true);
  };

  const handleCloseVisualization = () => {
    setVisualization(null);
    setIsPlaying(false);
    setPresentationMode(false);
  };

  const currentEvent = visualization?.events?.[currentStepIndex] || null;

  return (
    <div className="min-h-screen bg-[#0B0F19] text-gray-100 flex flex-col font-sans selection:bg-blue-500 selection:text-white">
      {/* Top Navigation */}
      <Header backendOnline={backendOnline} totalStations={stations.length} />

      {/* Main Container */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 lg:px-8 py-6 space-y-8">
        {/* Error Alert Banner */}
        {error && (
          <div className="bg-rose-950/40 border border-rose-500/40 text-rose-200 px-4 py-3 rounded-xl flex items-center justify-between shadow-lg">
            <div className="flex items-center gap-2.5 text-sm">
              <AlertTriangle className="w-5 h-5 text-rose-400 shrink-0" />
              <span>{error}</span>
            </div>
            <button
              onClick={() => setError(null)}
              className="text-xs text-rose-300 hover:text-white px-2 py-1 rounded bg-rose-900/60 hover:bg-rose-800 transition-colors cursor-pointer"
            >
              Dismiss
            </button>
          </div>
        )}

        {/* Loading State Skeleton */}
        {initialLoading ? (
          <div className="flex flex-col items-center justify-center py-24 space-y-4">
            <RefreshCw className="w-8 h-8 text-blue-500 animate-spin" />
            <div className="text-gray-300 font-medium">Connecting to Java Spring Boot Backend...</div>
            <div className="text-xs text-gray-500">Initializing in-memory metro graph and loading stations</div>
          </div>
        ) : (
          <>
            {/* PRESENTATION MODE: Full Width Immersive Layout */}
            {presentationMode && visualization ? (
              <div className="space-y-6">
                {/* Large Interactive Metro Map */}
                <MetroMap
                  stations={stations}
                  selectedRoute={routeResult}
                  onSelectStation={handleMapStationSelect}
                  isVisualizing={true}
                  currentEvent={currentEvent}
                  weightUnit={visualization.weightUnit}
                  presentationMode={true}
                  finalPath={visualization.finalPath}
                />

                {/* Step-by-Step Viva Presentation Controller */}
                <AlgorithmVisualizer
                  visualization={visualization}
                  currentStepIndex={currentStepIndex}
                  isPlaying={isPlaying}
                  playbackSpeed={playbackSpeed}
                  presentationMode={true}
                  onStepChange={handleStepChange}
                  onTogglePlay={handleTogglePlay}
                  onSpeedChange={handleSpeedChange}
                  onTogglePresentationMode={handleTogglePresentationMode}
                  onRestart={handleRestartVisualization}
                  onClose={handleCloseVisualization}
                />

                {/* Calculated Route Timeline & Stats */}
                {routeResult && <RouteResult result={routeResult} />}
              </div>
            ) : (
              /* STANDARD LAYOUT: Grid Column Layout */
              <div className="space-y-6">
                {/* Active Visualization Banner / Visualizer Panel */}
                {visualization && (
                  <div className="transition-all duration-300">
                    <AlgorithmVisualizer
                      visualization={visualization}
                      currentStepIndex={currentStepIndex}
                      isPlaying={isPlaying}
                      playbackSpeed={playbackSpeed}
                      presentationMode={false}
                      onStepChange={handleStepChange}
                      onTogglePlay={handleTogglePlay}
                      onSpeedChange={handleSpeedChange}
                      onTogglePresentationMode={handleTogglePresentationMode}
                      onRestart={handleRestartVisualization}
                      onClose={handleCloseVisualization}
                    />
                  </div>
                )}

                {/* Top Grid: Route Selector & Interactive Metro Map */}
                <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
                  {/* Left Column: Route Finder Form & Results (5 Cols on large screens) */}
                  <div className="lg:col-span-5 space-y-6">
                    <RouteSelector
                      stations={stations}
                      source={source}
                      destination={destination}
                      preference={preference}
                      loading={loading}
                      visualizing={visualizing}
                      onSourceChange={(val) => {
                        setSource(val);
                        setError(null);
                        setVisualization(null);
                        setIsPlaying(false);
                      }}
                      onDestinationChange={(val) => {
                        setDestination(val);
                        setError(null);
                        setVisualization(null);
                        setIsPlaying(false);
                      }}
                      onPreferenceChange={(pref) => {
                        setPreference(pref);
                        setVisualization(null);
                        setIsPlaying(false);
                        // Recompute if endpoints are selected
                        if (source && destination && source !== destination) {
                          setLoading(true);
                          findRoute(source, destination, pref)
                            .then((res) => setRouteResult(res))
                            .catch((err) => setError(err.message))
                            .finally(() => setLoading(false));
                        }
                      }}
                      onSwapStations={handleSwapStations}
                      onSubmit={handleCalculateRoute}
                      onStartVisualization={handleStartVisualization}
                      onSelectPreset={handleSelectPreset}
                    />

                    {/* Calculated Route Timeline & Stats */}
                    {routeResult && <RouteResult result={routeResult} />}
                  </div>

                  {/* Right Column: Interactive Metro Map (7 Cols on large screens) */}
                  <div className="lg:col-span-7">
                    <MetroMap
                      stations={stations}
                      selectedRoute={routeResult}
                      onSelectStation={handleMapStationSelect}
                      isVisualizing={!!visualization}
                      currentEvent={currentEvent}
                      weightUnit={visualization?.weightUnit || 'min'}
                      presentationMode={presentationMode}
                      finalPath={visualization?.finalPath || []}
                    />
                  </div>
                </div>
              </div>
            )}

            {/* Bottom Section: DSA Viva / How MetroPath Works */}
            <section className="pt-6 border-t border-gray-800/80">
              <AlgorithmInfo />
            </section>
          </>
        )}
      </main>

      {/* Footer */}
      <footer className="border-t border-gray-800 bg-gray-950/70 py-6 px-4 lg:px-8 mt-12 text-xs text-gray-500">
        <div className="max-w-7xl mx-auto flex flex-col sm:flex-row items-center justify-between gap-3 text-center sm:text-left">
          <div>
            <span className="font-semibold text-gray-400">MetroPath — Smart Metro Route Finder</span>
            <span className="mx-2">•</span>
            <span>College DSA Capstone Project</span>
          </div>
          <div className="font-mono text-gray-400">
            Backend: Java 17+ • Spring Boot • Adjacency List • BFS • Dijkstra
          </div>
        </div>
      </footer>
    </div>
  );
};

export default App;
