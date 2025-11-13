# Contributing to LocalAiChatApp

Thank you for your interest in contributing to LocalAiChatApp! This document provides guidelines and instructions for contributing.

## Table of Contents
- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [How to Contribute](#how-to-contribute)
- [Development Workflow](#development-workflow)
- [Coding Standards](#coding-standards)
- [Commit Guidelines](#commit-guidelines)
- [Pull Request Process](#pull-request-process)
- [Issue Guidelines](#issue-guidelines)
- [Community](#community)

## Code of Conduct

### Our Pledge

We are committed to providing a welcoming and inspiring community for all. Please be respectful and constructive in all interactions.

### Our Standards

**Positive behaviors**:
- Using welcoming and inclusive language
- Being respectful of differing viewpoints
- Gracefully accepting constructive criticism
- Focusing on what is best for the community
- Showing empathy towards others

**Unacceptable behaviors**:
- Harassment, trolling, or insulting comments
- Public or private harassment
- Publishing others' private information
- Other unethical or unprofessional conduct

### Enforcement

Instances of abusive, harassing, or otherwise unacceptable behavior may be reported by contacting the project team. All complaints will be reviewed and investigated promptly and fairly.

## Getting Started

### Prerequisites

Before contributing, ensure you have:

1. **Development Environment Set Up**
   - See [Development Setup Guide](./DEVELOPMENT_SETUP.md)
   - Android Studio Hedgehog or later
   - JDK 21
   - Git configured

2. **GitHub Account**
   - [Create account](https://github.com/signup) if you don't have one
   - [Set up SSH keys](https://docs.github.com/en/authentication/connecting-to-github-with-ssh)

3. **Familiarity with Project**
   - Read [README.md](../README.md)
   - Review [Architecture Documentation](./ARCHITECTURE.md)
   - Explore the codebase

### First-Time Setup

```bash
# Fork the repository on GitHub

# Clone your fork
git clone https://github.com/YOUR_USERNAME/Local-Multimodal-LLM-Ai-Chat.git
cd Local-Multimodal-LLM-Ai-Chat

# Add upstream remote
git remote add upstream https://github.com/ORIGINAL_OWNER/Local-Multimodal-LLM-Ai-Chat.git

# Create a branch for your changes
git checkout -b feature/your-feature-name
```

## How to Contribute

### Ways to Contribute

1. **Code Contributions**
   - Bug fixes
   - New features
   - Performance improvements
   - UI/UX enhancements

2. **Documentation**
   - Improve existing docs
   - Add examples
   - Fix typos
   - Translate documentation

3. **Testing**
   - Write unit tests
   - Add integration tests
   - Test on different devices
   - Report bugs

4. **Design**
   - UI/UX improvements
   - Icon design
   - Screenshots
   - Marketing materials

5. **Community**
   - Answer questions
   - Help other contributors
   - Review pull requests
   - Participate in discussions

### Finding Issues to Work On

- **Good First Issues**: Look for `good-first-issue` label
- **Help Wanted**: Issues labeled `help-wanted`
- **Bugs**: Check `bug` labeled issues
- **Features**: Look at `enhancement` labeled issues

**Before starting work**:
1. Comment on the issue to claim it
2. Wait for maintainer confirmation
3. Ask questions if anything is unclear

## Development Workflow

### 1. Create a Branch

```bash
# Update your main branch
git checkout main
git pull upstream main

# Create feature branch
git checkout -b feature/your-feature-name

# Branch naming conventions:
# - feature/feature-name    (new features)
# - fix/bug-description     (bug fixes)
# - docs/what-changed       (documentation)
# - refactor/what-changed   (code refactoring)
# - test/what-tested        (adding tests)
```

### 2. Make Changes

- Write clean, readable code
- Follow [Code Style Guide](./CODE_STYLE.md)
- Add tests for new functionality
- Update documentation as needed

### 3. Test Your Changes

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run lint checks
./gradlew lint

# Manual testing on emulator/device
```

### 4. Commit Changes

```bash
# Stage changes
git add .

# Commit with descriptive message
git commit -m "feat: add new feature"

# See commit guidelines below for message format
```

### 5. Push to Your Fork

```bash
# Push branch to your fork
git push origin feature/your-feature-name
```

### 6. Create Pull Request

1. Go to your fork on GitHub
2. Click "Compare & pull request"
3. Fill out PR template
4. Submit pull request

## Coding Standards

### Kotlin Style Guide

Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html):

```kotlin
// Good: Descriptive names
fun calculateTotalTokens(messages: List<ChatMessage>): Int {
    return messages.sumOf { it.tokenCount }
}

// Bad: Unclear names
fun calc(m: List<ChatMessage>): Int {
    return m.sumOf { it.tc }
}
```

### Architecture Principles

1. **Clean Architecture**
   - Keep layers separated
   - Dependencies point inward
   - Use dependency injection

2. **SOLID Principles**
   - Single Responsibility
   - Open/Closed
   - Liskov Substitution
   - Interface Segregation
   - Dependency Inversion

3. **Composition over Inheritance**

### Code Organization

```kotlin
// File structure order:
class MyClass {
    // 1. Companion object
    companion object {
        private const val TAG = "MyClass"
    }

    // 2. Properties
    private val repository: Repository

    // 3. Init blocks
    init {
        // Initialization
    }

    // 4. Public methods
    fun publicMethod() { }

    // 5. Private methods
    private fun privateMethod() { }
}
```

### Naming Conventions

```kotlin
// Classes: PascalCase
class ChatViewModel

// Functions: camelCase
fun generateResponse()

// Properties: camelCase
val messageList: List<Message>

// Constants: SCREAMING_SNAKE_CASE
const val MAX_RETRY_COUNT = 3

// Resources: snake_case
// res/layout/activity_main.xml
// res/drawable/ic_send.xml
```

### Comments and Documentation

```kotlin
/**
 * Generates AI response for the given input.
 *
 * @param input User's message text
 * @param modelId ID of the model to use
 * @param sessionId Current chat session ID
 * @return Flow emitting response chunks
 */
fun generateResponse(
    input: String,
    modelId: String,
    sessionId: String
): Flow<ResponseChunk>
```

### Error Handling

```kotlin
// Use Result wrapper for error handling
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

// Example usage
suspend fun fetchData(): Result<Data> {
    return try {
        val data = api.getData()
        Result.Success(data)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
```

### Testing

```kotlin
// Test naming: methodName_condition_expectedResult
@Test
fun generateResponse_withValidInput_returnsSuccess() {
    // Given
    val input = "Hello"

    // When
    val result = viewModel.generateResponse(input)

    // Then
    assertTrue(result is Result.Success)
}
```

## Commit Guidelines

### Commit Message Format

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- **feat**: New feature
- **fix**: Bug fix
- **docs**: Documentation changes
- **style**: Code style changes (formatting, etc.)
- **refactor**: Code refactoring
- **test**: Adding or updating tests
- **chore**: Maintenance tasks
- **perf**: Performance improvements

### Examples

```bash
# Simple feature
git commit -m "feat: add image analysis feature"

# Bug fix with scope
git commit -m "fix(chat): resolve message ordering issue"

# Breaking change
git commit -m "feat!: redesign chat interface

BREAKING CHANGE: Chat interface API has changed"

# With body
git commit -m "refactor: improve database query performance

- Added indexes to frequently queried columns
- Optimized JOIN operations
- Reduced query execution time by 40%"
```

### Commit Best Practices

1. **Keep commits atomic**: One logical change per commit
2. **Write clear messages**: Describe what and why, not how
3. **Reference issues**: Use `Fixes #123` or `Closes #456`
4. **Avoid generic messages**: Not "fix bug" or "update code"

## Pull Request Process

### Before Submitting

- [ ] Code follows project style guide
- [ ] All tests pass locally
- [ ] Added tests for new functionality
- [ ] Updated documentation
- [ ] Commit messages follow conventions
- [ ] Branch is up to date with main

### PR Title Format

Use same format as commit messages:

```
feat: add dark mode support
fix(database): resolve migration issue
docs: update installation guide
```

### PR Description Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Related Issues
Fixes #123
Closes #456

## Testing
- [ ] Unit tests added/updated
- [ ] Manual testing performed
- [ ] Tested on multiple API levels

## Screenshots (if applicable)
[Add screenshots]

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated
- [ ] No new warnings generated
- [ ] Tests pass locally
```

### Review Process

1. **Automated Checks**
   - CI/CD pipeline runs tests
   - Lint checks must pass
   - Build must succeed

2. **Code Review**
   - At least one maintainer approval required
   - Address review comments
   - Make requested changes

3. **Merge**
   - Squash and merge (usually)
   - Clean commit history
   - Maintainer merges PR

### Responding to Reviews

- Be open to feedback
- Ask for clarification if needed
- Make requested changes promptly
- Thank reviewers for their time

## Issue Guidelines

### Bug Reports

Use the bug report template:

```markdown
**Describe the bug**
Clear description of the bug

**To Reproduce**
Steps to reproduce:
1. Go to '...'
2. Click on '...'
3. See error

**Expected behavior**
What should happen

**Screenshots**
If applicable

**Environment**
- Device: [e.g., Pixel 6]
- Android Version: [e.g., Android 14]
- App Version: [e.g., 1.0.0]

**Additional context**
Any other relevant information
```

### Feature Requests

Use the feature request template:

```markdown
**Feature Description**
Clear description of the feature

**Problem it Solves**
What problem does this address?

**Proposed Solution**
How should it work?

**Alternatives Considered**
Other approaches you've thought about

**Additional Context**
Mockups, examples, etc.
```

### Issue Labels

- `bug`: Something isn't working
- `enhancement`: New feature or request
- `documentation`: Documentation improvements
- `good-first-issue`: Good for newcomers
- `help-wanted`: Extra attention needed
- `question`: Further information requested
- `wontfix`: This will not be worked on

## Community

### Communication Channels

- **GitHub Issues**: Bug reports and feature requests
- **GitHub Discussions**: General questions and ideas
- **Pull Requests**: Code review and collaboration

### Getting Help

1. **Check Documentation**: Review docs first
2. **Search Issues**: See if question already answered
3. **Ask in Discussions**: For general questions
4. **Create Issue**: For specific bugs or features

### Recognition

Contributors will be:
- Listed in CONTRIBUTORS.md
- Mentioned in release notes
- Credited in documentation

## Additional Resources

- [Development Setup](./DEVELOPMENT_SETUP.md)
- [Code Style Guide](./CODE_STYLE.md)
- [Testing Guide](./TESTING.md)
- [Architecture Documentation](./ARCHITECTURE.md)
- [API Reference](./API_REFERENCE.md)

## Questions?

Don't hesitate to ask questions:
- Open a discussion on GitHub
- Comment on relevant issues
- Reach out to maintainers

---

Thank you for contributing to LocalAiChatApp! Every contribution, no matter how small, helps make this project better. 🎉
