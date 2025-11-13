/**
 * Core TypeScript types for the web application.
 * These mirror the Kotlin domain models from the Android app.
 */

export enum TaskType {
  CHAT = 'CHAT',
  ASK_IMAGE = 'ASK_IMAGE',
  ASK_AUDIO = 'ASK_AUDIO',
  PROMPT_LAB = 'PROMPT_LAB'
}

export interface ChatSession {
  id: string;
  modelId: string;
  title: string;
  taskType: TaskType;
  createdAt: number;
  updatedAt: number;
  isBookmarked: boolean;
}

export interface ChatMessage {
  id: string;
  sessionId: string;
  content: string;
  isFromUser: boolean;
  timestamp: number;
  metadata?: ResponseMetadata;
}

export interface ResponseMetadata {
  tokenCount?: number;
  timeToFirstToken?: number;
  decodeSpeed?: number;
  totalLatency?: number;
}

export interface Model {
  id: string;
  name: string;
  size: number;
  description: string;
  status: ModelStatus;
  downloadProgress?: number;
  localPath?: string;
}

export enum ModelStatus {
  AVAILABLE = 'AVAILABLE',
  DOWNLOADING = 'DOWNLOADING',
  DOWNLOADED = 'DOWNLOADED',
  LOADING = 'LOADING',
  LOADED = 'LOADED',
  ERROR = 'ERROR'
}

export interface PromptTemplate {
  id: string;
  name: string;
  description: string;
  category: string;
  template: string;
  parameters: string[];
}

export interface Benchmark {
  id: string;
  modelId: string;
  sessionId?: string;
  timeToFirstToken: number;
  decodeSpeed: number;
  totalLatency: number;
  memoryUsage: number;
  cpuUsage: number;
  timestamp: number;
}

// Automation types
export enum TriggerType {
  MESSAGE_SENT = 'MESSAGE_SENT',
  MESSAGE_RECEIVED = 'MESSAGE_RECEIVED',
  SESSION_CREATED = 'SESSION_CREATED',
  MODEL_DOWNLOADED = 'MODEL_DOWNLOADED',
  APP_LAUNCHED = 'APP_LAUNCHED',
  SCHEDULED = 'SCHEDULED'
}

export enum ActionType {
  SHOW_NOTIFICATION = 'SHOW_NOTIFICATION',
  BACKUP_DATABASE = 'BACKUP_DATABASE',
  SHARE_TEXT = 'SHARE_TEXT',
  SAVE_TO_FILE = 'SAVE_TO_FILE'
}

export enum ConditionType {
  EQUALS = 'EQUALS',
  CONTAINS = 'CONTAINS',
  TIME_OF_DAY = 'TIME_OF_DAY',
  STORAGE_AVAILABLE = 'STORAGE_AVAILABLE'
}

export interface Workflow {
  id: string;
  name: string;
  description?: string;
  enabled: boolean;
  triggerType: TriggerType;
  triggerConfig: Record<string, any>;
  conditions: WorkflowCondition[];
  actions: WorkflowAction[];
  createdAt: number;
  updatedAt: number;
  executionCount: number;
}

export interface WorkflowCondition {
  id: string;
  type: ConditionType;
  config: Record<string, any>;
}

export interface WorkflowAction {
  id: string;
  type: ActionType;
  config: Record<string, any>;
  order: number;
}

export interface WorkflowExecution {
  id: string;
  workflowId: string;
  status: 'SUCCESS' | 'FAILED' | 'SKIPPED';
  startedAt: number;
  completedAt?: number;
  error?: string;
  logs: string[];
}

// UI State types
export interface ChatUiState {
  messages: ChatMessage[];
  isLoading: boolean;
  error?: string;
  inputText: string;
}

export interface AppSettings {
  theme: 'light' | 'dark' | 'system';
  defaultModel: string;
  maxResponseLength: number;
  temperature: number;
  enableStreaming: boolean;
  enableNotifications: boolean;
}

// API Response types
export type Result<T> =
  | { success: true; data: T }
  | { success: false; error: string };

export interface ResponseChunk {
  content: string;
  isComplete: boolean;
  metadata?: ResponseMetadata;
}
