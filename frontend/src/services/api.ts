export interface Station {
  id: number;
  name: string;
  line: string;
  latitude: number;
  longitude: number;
  interchange: boolean;
  lines: string[];
}

export interface RouteStep {
  stationId: number;
  stationName: string;
  line: string;
  cumulativeDistance: number;
  cumulativeTime: number;
  interchange: boolean;
  latitude: number;
  longitude: number;
}

export interface RouteResult {
  source: string;
  destination: string;
  stations: string[];
  stops: number;
  distance: number;
  time: number;
  interchanges: number;
  lines: string[];
  algorithmUsed: string;
  routeSteps: RouteStep[];
}

export type RoutePreference = 'FASTEST' | 'SHORTEST_DISTANCE' | 'FEWEST_STOPS';

const API_BASE_URL = 'http://localhost:8080/api';

/**
 * Fetch all registered stations from the Java Spring Boot backend.
 */
export async function fetchStations(): Promise<Station[]> {
  try {
    const response = await fetch(`${API_BASE_URL}/stations`);
    if (!response.ok) {
      throw new Error(`Failed to fetch stations (HTTP ${response.status})`);
    }
    const data: Station[] = await response.json();
    return data;
  } catch (error) {
    console.error('Error fetching stations:', error);
    throw error;
  }
}

/**
 * Query route calculation from the Java backend using Graph + BFS / Dijkstra.
 */
export async function findRoute(
  source: string,
  destination: string,
  preference: RoutePreference
): Promise<RouteResult> {
  try {
    const response = await fetch(`${API_BASE_URL}/route`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        source,
        destination,
        preference,
      }),
    });

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data.error || `Error ${response.status}: Failed to calculate route`);
    }

    return data as RouteResult;
  } catch (error) {
    console.error('Error finding route:', error);
    throw error;
  }
}

/**
 * Health check endpoint to ensure Spring Boot backend is UP.
 */
export async function checkBackendHealth(): Promise<boolean> {
  try {
    const response = await fetch(`${API_BASE_URL}/health`);
    if (!response.ok) return false;
    const data = await response.json();
    return data.status === 'UP';
  } catch {
    return false;
  }
}
