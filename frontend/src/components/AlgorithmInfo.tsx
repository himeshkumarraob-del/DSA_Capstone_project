import React from 'react';
import { Network, Database, GitBranch, Cpu, ListOrdered, Hash, RotateCcw, Clock } from 'lucide-react';

export const AlgorithmInfo: React.FC = () => {
  return (
    <div className="space-y-8">
      {/* Title */}
      <div className="text-center max-w-3xl mx-auto space-y-2">
        <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-blue-500/10 border border-blue-500/20 text-blue-400 text-xs font-semibold">
          <Cpu className="w-3.5 h-3.5" />
          <span>Core DSA Architecture</span>
        </div>
        <h2 className="text-2xl sm:text-3xl font-bold text-white tracking-tight">
          How MetroPath Works
        </h2>
        <p className="text-sm text-gray-400">
          MetroPath implements fundamental Computer Science Data Structures & Algorithms in pure Java 17, executing all routing calculations on-demand in memory.
        </p>
      </div>

      {/* 7 Core DSA Architecture Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {/* 1. Graph */}
        <div className="bg-gray-900/90 border border-gray-800 rounded-xl p-5 space-y-3 hover:border-gray-700 transition-colors">
          <div className="w-10 h-10 rounded-lg bg-blue-500/10 border border-blue-500/20 flex items-center justify-center text-blue-400">
            <Network className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-base font-bold text-white">1. Graph Representation</h3>
            <p className="text-xs text-gray-400 mt-1">
              The metro network is modeled as an <strong>Undirected Weighted Graph</strong> <code className="text-blue-300">G = (V, E)</code>. Each station is a <strong>Vertex</strong>, and physical tracks are bidirectional <strong>Edges</strong> storing distance (km), travel time (min), and line identity.
            </p>
          </div>
        </div>

        {/* 2. Adjacency List */}
        <div className="bg-gray-900/90 border border-gray-800 rounded-xl p-5 space-y-3 hover:border-gray-700 transition-colors">
          <div className="w-10 h-10 rounded-lg bg-indigo-500/10 border border-indigo-500/20 flex items-center justify-center text-indigo-400">
            <Database className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-base font-bold text-white">2. Adjacency List</h3>
            <p className="text-xs text-gray-400 mt-1">
              Implemented using <code className="text-indigo-300">Map&lt;Integer, List&lt;Edge&gt;&gt;</code>. Enables fast iteration over neighbor vertices in <code className="text-indigo-300">O(degree(v))</code> time, saving significant memory compared to an adjacency matrix for sparse transit networks.
            </p>
          </div>
        </div>

        {/* 3. BFS */}
        <div className="bg-gray-900/90 border border-gray-800 rounded-xl p-5 space-y-3 hover:border-gray-700 transition-colors">
          <div className="w-10 h-10 rounded-lg bg-purple-500/10 border border-purple-500/20 flex items-center justify-center text-purple-400">
            <GitBranch className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-base font-bold text-white">3. Breadth-First Search (BFS)</h3>
            <p className="text-xs text-gray-400 mt-1">
              Used for <strong>Fewest Stops</strong> mode. Uses a <code className="text-purple-300">Queue&lt;Integer&gt;</code> and a <code className="text-purple-300">boolean[] visited</code> array to traverse level-by-level, mathematically guaranteeing the path with the minimum number of station hops.
            </p>
          </div>
        </div>

        {/* 4. Dijkstra's Algorithm */}
        <div className="bg-gray-900/90 border border-gray-800 rounded-xl p-5 space-y-3 hover:border-gray-700 transition-colors">
          <div className="w-10 h-10 rounded-lg bg-emerald-500/10 border border-emerald-500/20 flex items-center justify-center text-emerald-400">
            <Clock className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-base font-bold text-white">4. Dijkstra's Algorithm</h3>
            <p className="text-xs text-gray-400 mt-1">
              Powers <strong>Fastest Route</strong> (edge weight = minutes) and <strong>Shortest Distance</strong> (edge weight = km). Greedily relaxes edge weights to discover the globally optimal lowest-cost path.
            </p>
          </div>
        </div>

        {/* 5. Priority Queue */}
        <div className="bg-gray-900/90 border border-gray-800 rounded-xl p-5 space-y-3 hover:border-gray-700 transition-colors">
          <div className="w-10 h-10 rounded-lg bg-amber-500/10 border border-amber-500/20 flex items-center justify-center text-amber-400">
            <ListOrdered className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-base font-bold text-white">5. Min-Heap Priority Queue</h3>
            <p className="text-xs text-gray-400 mt-1">
              Java's <code className="text-amber-300">PriorityQueue&lt;Node&gt;</code> maintains the tentative shortest distances. The <code className="text-amber-300">Comparable&lt;Node&gt;</code> interface extracts the minimum cost station in <code className="text-amber-300">O(log V)</code> time.
            </p>
          </div>
        </div>

        {/* 6. HashMap Lookup */}
        <div className="bg-gray-900/90 border border-gray-800 rounded-xl p-5 space-y-3 hover:border-gray-700 transition-colors">
          <div className="w-10 h-10 rounded-lg bg-rose-500/10 border border-rose-500/20 flex items-center justify-center text-rose-400">
            <Hash className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-base font-bold text-white">6. HashMap Indexing</h3>
            <p className="text-xs text-gray-400 mt-1">
              <code className="text-rose-300">HashMap&lt;String, Integer&gt;</code> converts human station names received from the REST request into internal graph vertex IDs in instantaneous <code className="text-rose-300">O(1)</code> average time.
            </p>
          </div>
        </div>

        {/* 7. Parent Array & Path Reconstruction */}
        <div className="bg-gray-900/90 border border-gray-800 rounded-xl p-5 space-y-3 md:col-span-2 lg:col-span-3 hover:border-gray-700 transition-colors">
          <div className="w-10 h-10 rounded-lg bg-cyan-500/10 border border-cyan-500/20 flex items-center justify-center text-cyan-400">
            <RotateCcw className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-base font-bold text-white">7. Parent Array & Route Reconstruction</h3>
            <p className="text-xs text-gray-400 mt-1">
              An array <code className="text-cyan-300">int[] parent</code> records the predecessor vertex <code className="text-cyan-300">parent[v] = u</code> whenever an edge is relaxed (Dijkstra) or unvisited vertex is enqueued (BFS). Once destination is reached, the application backtracks <code className="text-cyan-300">destination → parent → source</code> and reverses the sequence to output the journey.
            </p>
          </div>
        </div>
      </div>

      {/* Complexity Analysis Table */}
      <div className="bg-gray-900/90 border border-gray-800 rounded-2xl p-6 shadow-xl backdrop-blur-md">
        <h3 className="text-lg font-bold text-white tracking-tight mb-4 flex items-center gap-2">
          <span>Algorithmic Complexity Summary</span>
          <span className="text-xs font-normal text-gray-400 font-mono">(Viva Ready Reference)</span>
        </h3>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead>
              <tr className="border-b border-gray-800 text-gray-400 text-xs uppercase tracking-wider">
                <th className="pb-3 font-semibold">Algorithm</th>
                <th className="pb-3 font-semibold">Optimization Target</th>
                <th className="pb-3 font-semibold">Time Complexity</th>
                <th className="pb-3 font-semibold">Space Complexity</th>
                <th className="pb-3 font-semibold">Key Java Data Structure</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-800/60 text-xs">
              <tr className="hover:bg-gray-800/30 transition-colors">
                <td className="py-3 font-semibold text-purple-400">BFS (Fewest Stops)</td>
                <td className="py-3 text-gray-300">Minimum number of station hops</td>
                <td className="py-3 font-mono text-purple-300">O(V + E)</td>
                <td className="py-3 font-mono text-purple-300">O(V)</td>
                <td className="py-3 text-gray-400 font-mono">Queue&lt;Integer&gt;, boolean[], int[]</td>
              </tr>
              <tr className="hover:bg-gray-800/30 transition-colors">
                <td className="py-3 font-semibold text-blue-400">Dijkstra (Fastest)</td>
                <td className="py-3 text-gray-300">Minimum total travel time (min)</td>
                <td className="py-3 font-mono text-blue-300">O((V + E) log V)</td>
                <td className="py-3 font-mono text-blue-300">O(V + E)</td>
                <td className="py-3 text-gray-400 font-mono">PriorityQueue&lt;Node&gt;, double[]</td>
              </tr>
              <tr className="hover:bg-gray-800/30 transition-colors">
                <td className="py-3 font-semibold text-emerald-400">Dijkstra (Shortest)</td>
                <td className="py-3 text-gray-300">Minimum physical distance (km)</td>
                <td className="py-3 font-mono text-emerald-300">O((V + E) log V)</td>
                <td className="py-3 font-mono text-emerald-300">O(V + E)</td>
                <td className="py-3 text-gray-400 font-mono">PriorityQueue&lt;Node&gt;, double[]</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div className="mt-4 pt-3 border-t border-gray-800 text-xs text-gray-400 flex flex-wrap items-center justify-between gap-2">
          <div>
            <strong className="text-gray-300">V</strong> = Number of Station Vertices (35) •{' '}
            <strong className="text-gray-300">E</strong> = Number of Track Connections (38 undirected edges, incl. 2 Orange Express)
          </div>
          <div className="text-blue-400 font-mono">
            Pure In-Memory Graph • Zero external shortest-path libraries
          </div>
        </div>
      </div>
    </div>
  );
};
