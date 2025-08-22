package com.localllm.localaichatapp.data.service

import com.localllm.localaichatapp.domain.model.PromptTemplate
import com.localllm.localaichatapp.domain.model.PromptCategory
import com.localllm.localaichatapp.domain.repository.PromptTemplateRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromptTemplateInitializer @Inject constructor(
    private val promptTemplateRepository: PromptTemplateRepository
) {
    
    suspend fun initializeDefaultTemplates() {
        // Check if templates already exist
        val existingTemplates = promptTemplateRepository.getAllTemplates().first()
        if (existingTemplates.isNotEmpty()) {
            return // Templates already initialized
        }
        
        // Initialize built-in templates
        getBuiltInTemplates().forEach { template ->
            promptTemplateRepository.insertTemplate(template)
        }
    }
    
    private fun getBuiltInTemplates(): List<PromptTemplate> {
        return listOf(
            // Writing & Content Creation
            PromptTemplate(
                id = "creative_writing",
                name = "Creative Writing Assistant",
                description = "Help with creative writing, storytelling, and narrative development",
                category = PromptCategory.CREATIVE,
                template = """You are a creative writing assistant. Help me with: {task}

Writing type: {type}
Genre: {genre}
Tone: {tone}
Target audience: {audience}

Requirements:
- Be creative and engaging
- Maintain consistency in style
- Provide detailed and vivid descriptions
- Focus on character development and plot progression

Please help me with this writing task.""",
                parameters = listOf("task", "type", "genre", "tone", "audience"),
                isBuiltIn = true,
                usageCount = 0,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            
            PromptTemplate(
                id = "blog_post_writer",
                name = "Blog Post Writer",
                description = "Create engaging blog posts and articles",
                category = PromptCategory.CREATIVE,
                template = """Write a compelling blog post about: {topic}

Target audience: {audience}
Writing style: {style}
Word count: {word_count}
Key points to cover: {key_points}

Structure:
- Engaging headline and introduction
- Clear main points with examples
- Actionable insights
- Strong conclusion with call-to-action

Please create a well-structured and engaging blog post.""",
                parameters = listOf("topic", "audience", "style", "word_count", "key_points"),
                isBuiltIn = true,
                usageCount = 0,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            
            // Business & Professional
            PromptTemplate(
                id = "email_composer",
                name = "Professional Email Composer",
                description = "Draft professional emails for various business contexts",
                category = PromptCategory.ANALYSIS,
                template = """Compose a professional email for: {purpose}

Recipient: {recipient}
Tone: {tone}
Key message: {message}
Required action: {action}
Deadline: {deadline}

Guidelines:
- Use appropriate business etiquette
- Be clear and concise
- Include relevant details
- Professional yet friendly tone
- Clear call-to-action

Please draft the email.""",
                parameters = listOf("purpose", "recipient", "tone", "message", "action", "deadline"),
                isBuiltIn = true,
                usageCount = 0,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            
            PromptTemplate(
                id = "meeting_agenda",
                name = "Meeting Agenda Creator",
                description = "Create structured meeting agendas and plans",
                category = PromptCategory.ANALYSIS,
                template = """Create a meeting agenda for: {meeting_purpose}

Meeting details:
- Date & Time: {date_time}
- Duration: {duration}
- Attendees: {attendees}
- Objectives: {objectives}
- Key topics: {topics}

Format:
- Welcome and introductions
- Agenda review
- Main discussion points with time allocations
- Action items and next steps
- Meeting wrap-up

Please create a structured agenda.""",
                parameters = listOf("meeting_purpose", "date_time", "duration", "attendees", "objectives", "topics"),
                isBuiltIn = true,
                usageCount = 0,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            
            // Education & Learning
            PromptTemplate(
                id = "study_guide",
                name = "Study Guide Generator",
                description = "Create comprehensive study guides and learning materials",
                category = PromptCategory.ANALYSIS,
                template = """Create a study guide for: {subject}

Topic: {topic}
Learning level: {level}
Study duration: {duration}
Learning objectives: {objectives}
Key concepts: {concepts}

Include:
- Overview and introduction
- Key concepts and definitions
- Important formulas/principles
- Practice questions
- Summary and review points
- Additional resources

Please create a comprehensive study guide.""",
                parameters = listOf("subject", "topic", "level", "duration", "objectives", "concepts"),
                isBuiltIn = true,
                usageCount = 0,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            
            PromptTemplate(
                id = "lesson_planner",
                name = "Lesson Plan Creator",
                description = "Design engaging lesson plans for educators",
                category = PromptCategory.ANALYSIS,
                template = """Design a lesson plan for: {subject}

Grade level: {grade}
Duration: {duration}
Learning objectives: {objectives}
Key topics: {topics}
Available resources: {resources}

Structure:
- Lesson overview and objectives
- Pre-requisites and preparation
- Introduction/hook activity
- Main instruction with activities
- Assessment methods
- Homework/follow-up activities

Please create an engaging lesson plan.""",
                parameters = listOf("subject", "grade", "duration", "objectives", "topics", "resources"),
                isBuiltIn = true,
                usageCount = 0,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            
            // Technical & Development
            PromptTemplate(
                id = "code_reviewer",
                name = "Code Review Assistant",
                description = "Analyze code for best practices, bugs, and improvements",
                category = PromptCategory.CODE_GEN,
                template = """Review this code for: {review_focus}

Programming language: {language}
Code purpose: {purpose}
Specific concerns: {concerns}

Review criteria:
- Code quality and readability
- Performance optimization
- Security considerations
- Best practices adherence
- Potential bugs or issues
- Suggestions for improvement

Code to review:
{code}

Please provide a detailed code review.""",
                parameters = listOf("review_focus", "language", "purpose", "concerns", "code"),
                isBuiltIn = true,
                usageCount = 0,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            
            PromptTemplate(
                id = "api_documentation",
                name = "API Documentation Generator",
                description = "Create comprehensive API documentation",
                category = PromptCategory.CODE_GEN,
                template = """Generate API documentation for: {api_name}

API details:
- Base URL: {base_url}
- Version: {version}
- Authentication: {auth_method}
- Endpoints: {endpoints}
- Data formats: {formats}

Include:
- API overview and purpose
- Authentication requirements
- Endpoint descriptions with examples
- Request/response formats
- Error codes and handling
- Rate limiting information
- SDK/code examples

Please create comprehensive API documentation.""",
                parameters = listOf("api_name", "base_url", "version", "auth_method", "endpoints", "formats"),
                isBuiltIn = true,
                usageCount = 0,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            
            // Marketing & Sales
            PromptTemplate(
                id = "marketing_copy",
                name = "Marketing Copy Creator",
                description = "Create compelling marketing content and copy",
                category = PromptCategory.CREATIVE,
                template = """Create marketing copy for: {product_service}

Campaign details:
- Target audience: {audience}
- Key benefits: {benefits}
- Unique selling proposition: {usp}
- Call-to-action: {cta}
- Tone: {tone}
- Platform: {platform}

Requirements:
- Attention-grabbing headline
- Compelling value proposition
- Address pain points
- Clear benefits and features
- Strong call-to-action
- Appropriate length for platform

Please create persuasive marketing copy.""",
                parameters = listOf("product_service", "audience", "benefits", "usp", "cta", "tone", "platform"),
                isBuiltIn = true,
                usageCount = 0,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            
            // Creative & Design
            PromptTemplate(
                id = "creative_brainstorm",
                name = "Creative Brainstorming Assistant",
                description = "Generate creative ideas and concepts",
                category = PromptCategory.CREATIVE,
                template = """Help me brainstorm ideas for: {project}

Project context:
- Goal: {goal}
- Target audience: {audience}
- Constraints: {constraints}
- Inspiration: {inspiration}
- Style preferences: {style}

Brainstorming focus:
- Generate diverse and creative ideas
- Consider different approaches and angles
- Think outside conventional boundaries
- Provide reasoning for each concept
- Suggest implementation possibilities

Please provide creative brainstorming ideas.""",
                parameters = listOf("project", "goal", "audience", "constraints", "inspiration", "style"),
                isBuiltIn = true,
                usageCount = 0,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}