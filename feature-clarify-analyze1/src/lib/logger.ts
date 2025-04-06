/**
 * Logger utility for the frontend application
 * Provides consistent logging with timestamps and log levels
 */

type LogLevel = 'debug' | 'info' | 'warn' | 'error';

interface LogOptions {
  // Whether to include timestamp in logs
  timestamp?: boolean;
  // Whether to log to console
  console?: boolean;
  // Whether to store logs in localStorage
  localStorage?: boolean;
  // Maximum number of logs to keep in localStorage
  maxLogs?: number;
}

const DEFAULT_OPTIONS: LogOptions = {
  timestamp: true,
  console: true,
  localStorage: true,
  maxLogs: 1000,
};

class Logger {
  private options: LogOptions;
  private readonly STORAGE_KEY = 'feature-clarify-analyze-logs';

  constructor(options: LogOptions = {}) {
    this.options = { ...DEFAULT_OPTIONS, ...options };
  }

  /**
   * Format a log message with timestamp if enabled
   */
  private formatMessage(level: LogLevel, message: string, data?: any): string {
    const timestamp = this.options.timestamp ? `[${new Date().toISOString()}] ` : '';
    const dataStr = data ? ` ${JSON.stringify(data)}` : '';
    return `${timestamp}[${level.toUpperCase()}] ${message}${dataStr}`;
  }

  /**
   * Store log in localStorage if enabled
   */
  private storeLog(level: LogLevel, message: string, data?: any): void {
    if (!this.options.localStorage) return;

    try {
      const logs = this.getLogs();
      const newLog = {
        timestamp: new Date().toISOString(),
        level,
        message,
        data: data ? JSON.stringify(data) : undefined,
      };

      logs.push(newLog);

      // Trim logs if exceeding max count
      if (logs.length > (this.options.maxLogs || DEFAULT_OPTIONS.maxLogs!)) {
        logs.splice(0, logs.length - (this.options.maxLogs || DEFAULT_OPTIONS.maxLogs!));
      }

      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(logs));
    } catch (error) {
      console.error('Failed to store log in localStorage:', error);
    }
  }

  /**
   * Get all logs from localStorage
   */
  public getLogs(): any[] {
    try {
      const logsStr = localStorage.getItem(this.STORAGE_KEY);
      return logsStr ? JSON.parse(logsStr) : [];
    } catch (error) {
      console.error('Failed to retrieve logs from localStorage:', error);
      return [];
    }
  }

  /**
   * Clear all logs from localStorage
   */
  public clearLogs(): void {
    try {
      localStorage.removeItem(this.STORAGE_KEY);
    } catch (error) {
      console.error('Failed to clear logs from localStorage:', error);
    }
  }

  /**
   * Log a debug message
   */
  public debug(message: string, data?: any): void {
    const formattedMessage = this.formatMessage('debug', message, data);
    if (this.options.console) {
      console.debug(formattedMessage);
    }
    this.storeLog('debug', message, data);
  }

  /**
   * Log an info message
   */
  public info(message: string, data?: any): void {
    const formattedMessage = this.formatMessage('info', message, data);
    if (this.options.console) {
      console.info(formattedMessage);
    }
    this.storeLog('info', message, data);
  }

  /**
   * Log a warning message
   */
  public warn(message: string, data?: any): void {
    const formattedMessage = this.formatMessage('warn', message, data);
    if (this.options.console) {
      console.warn(formattedMessage);
    }
    this.storeLog('warn', message, data);
  }

  /**
   * Log an error message
   */
  public error(message: string, data?: any): void {
    const formattedMessage = this.formatMessage('error', message, data);
    if (this.options.console) {
      console.error(formattedMessage);
    }
    this.storeLog('error', message, data);
  }

  /**
   * Export logs as JSON
   */
  public exportLogs(): string {
    return JSON.stringify(this.getLogs(), null, 2);
  }

  /**
   * Download logs as a JSON file
   */
  public downloadLogs(): void {
    const logs = this.exportLogs();
    const blob = new Blob([logs], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `feature-clarify-analyze-logs-${new Date().toISOString()}.json`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  }
}

// Create and export a singleton instance
const logger = new Logger();
export default logger;
