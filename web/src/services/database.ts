/**
 * IndexedDB wrapper using Dexie for local data storage.
 * Mirrors the Room database structure from Android.
 */

import Dexie, { Table } from 'dexie';
import {
  ChatSession,
  ChatMessage,
  Model,
  PromptTemplate,
  Benchmark,
  Workflow,
  WorkflowExecution
} from '@types/index';

export class ChatDatabase extends Dexie {
  sessions!: Table<ChatSession, string>;
  messages!: Table<ChatMessage, string>;
  models!: Table<Model, string>;
  templates!: Table<PromptTemplate, string>;
  benchmarks!: Table<Benchmark, string>;
  workflows!: Table<Workflow, string>;
  executions!: Table<WorkflowExecution, string>;

  constructor() {
    super('LocalAiChatDB');

    // Database version 4 to match Android app
    this.version(4).stores({
      sessions: 'id, modelId, taskType, createdAt, updatedAt, isBookmarked',
      messages: 'id, sessionId, timestamp, isFromUser',
      models: 'id, status, name',
      templates: 'id, category, name',
      benchmarks: 'id, modelId, sessionId, timestamp',
      workflows: 'id, triggerType, enabled, createdAt',
      executions: 'id, workflowId, status, startedAt'
    });
  }

  /**
   * Clear all data from the database.
   */
  async clearAll(): Promise<void> {
    await this.transaction('rw', this.tables, async () => {
      for (const table of this.tables) {
        await table.clear();
      }
    });
  }

  /**
   * Export database to JSON for backup/transfer.
   */
  async exportToJSON(): Promise<string> {
    const data = {
      sessions: await this.sessions.toArray(),
      messages: await this.messages.toArray(),
      models: await this.models.toArray(),
      templates: await this.templates.toArray(),
      benchmarks: await this.benchmarks.toArray(),
      workflows: await this.workflows.toArray(),
      executions: await this.executions.toArray(),
      exportedAt: Date.now(),
      version: 4
    };

    return JSON.stringify(data, null, 2);
  }

  /**
   * Import database from JSON.
   */
  async importFromJSON(jsonData: string): Promise<void> {
    const data = JSON.parse(jsonData);

    await this.transaction('rw', this.tables, async () => {
      // Clear existing data
      await this.clearAll();

      // Import data
      if (data.sessions) await this.sessions.bulkAdd(data.sessions);
      if (data.messages) await this.messages.bulkAdd(data.messages);
      if (data.models) await this.models.bulkAdd(data.models);
      if (data.templates) await this.templates.bulkAdd(data.templates);
      if (data.benchmarks) await this.benchmarks.bulkAdd(data.benchmarks);
      if (data.workflows) await this.workflows.bulkAdd(data.workflows);
      if (data.executions) await this.executions.bulkAdd(data.executions);
    });
  }

  /**
   * Get database statistics.
   */
  async getStats() {
    return {
      sessions: await this.sessions.count(),
      messages: await this.messages.count(),
      models: await this.models.count(),
      templates: await this.templates.count(),
      benchmarks: await this.benchmarks.count(),
      workflows: await this.workflows.count(),
      executions: await this.executions.count()
    };
  }
}

// Singleton instance
export const db = new ChatDatabase();

// Initialize with default prompt templates
export async function initializeDefaultData() {
  const templateCount = await db.templates.count();

  if (templateCount === 0) {
    const defaultTemplates: PromptTemplate[] = [
      {
        id: 'summarize-text',
        name: 'Summarize Text',
        description: 'Summarize long text into key points',
        category: 'Writing',
        template: 'Please summarize the following text:\n\n{{text}}',
        parameters: ['text']
      },
      {
        id: 'explain-code',
        name: 'Explain Code',
        description: 'Explain what code does',
        category: 'Technical',
        template: 'Explain what this code does:\n\n```\n{{code}}\n```',
        parameters: ['code']
      },
      {
        id: 'write-email',
        name: 'Write Email',
        description: 'Draft a professional email',
        category: 'Writing',
        template: 'Write a professional email about {{topic}} to {{recipient}}',
        parameters: ['topic', 'recipient']
      },
      {
        id: 'brainstorm-ideas',
        name: 'Brainstorm Ideas',
        description: 'Generate creative ideas',
        category: 'Creative',
        template: 'Brainstorm 5 creative ideas for {{topic}}',
        parameters: ['topic']
      },
      {
        id: 'translate-text',
        name: 'Translate Text',
        description: 'Translate text to another language',
        category: 'Language',
        template: 'Translate the following text to {{language}}:\n\n{{text}}',
        parameters: ['language', 'text']
      }
    ];

    await db.templates.bulkAdd(defaultTemplates);
  }
}
