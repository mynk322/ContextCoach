import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Download, Trash2, RefreshCw } from "lucide-react";
import logger from "@/lib/logger";

interface Log {
  timestamp: string;
  level: 'debug' | 'info' | 'warn' | 'error';
  message: string;
  data?: string;
}

export function LogsViewer() {
  const [logs, setLogs] = useState<Log[]>([]);
  const [filter, setFilter] = useState<'all' | 'debug' | 'info' | 'warn' | 'error'>('all');
  const [isRefreshing, setIsRefreshing] = useState(false);

  // Load logs on mount and when refreshed
  useEffect(() => {
    loadLogs();
  }, []);

  const loadLogs = () => {
    setIsRefreshing(true);
    try {
      const allLogs = logger.getLogs();
      setLogs(allLogs);
    } catch (error) {
      console.error("Failed to load logs:", error);
    } finally {
      setIsRefreshing(false);
    }
  };

  const clearLogs = () => {
    if (window.confirm("Are you sure you want to clear all logs?")) {
      logger.clearLogs();
      setLogs([]);
    }
  };

  const downloadLogs = () => {
    logger.downloadLogs();
  };

  const getLogLevelColor = (level: string) => {
    switch (level) {
      case 'debug': return "bg-gray-500/10 text-gray-600 dark:text-gray-400";
      case 'info': return "bg-blue-500/10 text-blue-600 dark:text-blue-400";
      case 'warn': return "bg-yellow-500/10 text-yellow-600 dark:text-yellow-400";
      case 'error': return "bg-red-500/10 text-red-600 dark:text-red-400";
      default: return "bg-gray-500/10 text-gray-600 dark:text-gray-400";
    }
  };

  const filteredLogs = filter === 'all' 
    ? logs 
    : logs.filter(log => log.level === filter);

  return (
    <Card className="flex flex-col h-full">
      <div className="p-4 border-b flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
        <div className="flex items-center">
          <h2 className="text-lg font-medium">Application Logs</h2>
          <Badge className="ml-2" variant="outline">
            {logs.length} entries
          </Badge>
        </div>
        
        <div className="flex items-center space-x-2">
          <div className="flex space-x-1">
            <Button
              size="sm"
              variant={filter === 'all' ? "default" : "outline"}
              onClick={() => setFilter('all')}
            >
              All
            </Button>
            <Button
              size="sm"
              variant={filter === 'debug' ? "default" : "outline"}
              onClick={() => setFilter('debug')}
            >
              Debug
            </Button>
            <Button
              size="sm"
              variant={filter === 'info' ? "default" : "outline"}
              onClick={() => setFilter('info')}
            >
              Info
            </Button>
            <Button
              size="sm"
              variant={filter === 'warn' ? "default" : "outline"}
              onClick={() => setFilter('warn')}
            >
              Warn
            </Button>
            <Button
              size="sm"
              variant={filter === 'error' ? "default" : "outline"}
              onClick={() => setFilter('error')}
            >
              Error
            </Button>
          </div>
          
          <Button
            size="sm"
            variant="outline"
            onClick={loadLogs}
            disabled={isRefreshing}
          >
            {isRefreshing ? (
              <RefreshCw className="h-4 w-4 animate-spin" />
            ) : (
              <RefreshCw className="h-4 w-4" />
            )}
            <span className="sr-only">Refresh</span>
          </Button>
          
          <Button
            size="sm"
            variant="outline"
            onClick={downloadLogs}
          >
            <Download className="h-4 w-4" />
            <span className="sr-only">Download</span>
          </Button>
          
          <Button
            size="sm"
            variant="outline"
            onClick={clearLogs}
          >
            <Trash2 className="h-4 w-4" />
            <span className="sr-only">Clear</span>
          </Button>
        </div>
      </div>
      
      <div className="flex-1 overflow-y-auto p-4">
        {filteredLogs.length === 0 ? (
          <div className="flex items-center justify-center h-full text-muted-foreground">
            No logs to display
          </div>
        ) : (
          <div className="space-y-2">
            {filteredLogs.map((log, index) => (
              <div 
                key={index} 
                className="p-3 border rounded-md text-sm"
              >
                <div className="flex items-center justify-between mb-1">
                  <Badge 
                    className={getLogLevelColor(log.level)} 
                    variant="outline"
                  >
                    {log.level.toUpperCase()}
                  </Badge>
                  <span className="text-xs text-muted-foreground">
                    {new Date(log.timestamp).toLocaleString()}
                  </span>
                </div>
                <p className="font-medium">{log.message}</p>
                {log.data && (
                  <pre className="mt-2 p-2 bg-muted/50 rounded text-xs overflow-x-auto">
                    {log.data}
                  </pre>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </Card>
  );
}
