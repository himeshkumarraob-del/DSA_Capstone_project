import React from 'react';
import { Train, ShieldCheck, AlertCircle } from 'lucide-react';

interface HeaderProps {
  backendOnline: boolean;
  totalStations: number;
}

export const Header: React.FC<HeaderProps> = ({ backendOnline, totalStations }) => {
  return (
    <header className="border-b border-gray-800 bg-gray-950/80 backdrop-blur sticky top-0 z-50 px-4 lg:px-8 py-3.5">
      <div className="max-w-7xl mx-auto flex flex-col sm:flex-row items-center justify-between gap-3">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-blue-600 via-indigo-600 to-purple-600 flex items-center justify-center shadow-lg shadow-blue-500/20">
            <Train className="w-6 h-6 text-white" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-xl font-bold tracking-tight text-white flex items-center gap-1.5">
                MetroPath
              </h1>
              <span className="text-[11px] font-semibold tracking-wide uppercase px-2 py-0.5 rounded-full bg-blue-500/10 text-blue-400 border border-blue-500/20">
                DSA Capstone
              </span>
            </div>
            <p className="text-xs text-gray-400">
              Smart Metro Route Finder • Java 17 Engine & In-Memory Graph
            </p>
          </div>
        </div>

        <div className="flex items-center gap-3">
          <div className="hidden md:flex items-center gap-1.5 text-xs text-gray-400 bg-gray-900/90 border border-gray-800 rounded-lg px-3 py-1.5">
            <span className="font-medium text-gray-300">Dataset:</span>
            <span>Chennai Metro (Demo)</span>
            <span className="text-gray-600">•</span>
            <span className="text-blue-400 font-mono font-semibold">{totalStations}</span>
            <span>Stations</span>
          </div>

          <div
            className={`flex items-center gap-2 text-xs px-3 py-1.5 rounded-lg border font-medium transition-colors ${
              backendOnline
                ? 'bg-emerald-500/10 border-emerald-500/30 text-emerald-400'
                : 'bg-rose-500/10 border-rose-500/30 text-rose-400'
            }`}
          >
            {backendOnline ? (
              <>
                <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
                <span className="flex items-center gap-1">
                  <ShieldCheck className="w-3.5 h-3.5" />
                  Spring Boot UP
                </span>
              </>
            ) : (
              <>
                <AlertCircle className="w-3.5 h-3.5 text-rose-400" />
                <span>Backend Offline (Port 8080)</span>
              </>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};
