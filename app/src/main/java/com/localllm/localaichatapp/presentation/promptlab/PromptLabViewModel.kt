package com.localllm.localaichatapp.presentation.promptlab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localllm.localaichatapp.domain.model.Model
import com.localllm.localaichatapp.domain.model.PromptTemplate
import com.localllm.localaichatapp.domain.model.ResponseMetadata
import com.localllm.localaichatapp.domain.repository.AiInferenceRepository
import com.localllm.localaichatapp.domain.repository.ModelRepository
import com.localllm.localaichatapp.domain.repository.PromptTemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PromptLabUiState(
    val templates: List<PromptTemplate> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val filteredTemplates: List<PromptTemplate> = emptyList(),
    val selectedTemplate: PromptTemplate? = null,
    val parameterValues: Map<String, String> = emptyMap(),
    val processedPrompt: String = "",
    val response: String = "",
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val modelName: String = "",
    val currentModel: Model? = null,
    val responseMetadata: ResponseMetadata? = null,
    val error: String? = null
)

@HiltViewModel
class PromptLabViewModel @Inject constructor(
    private val promptTemplateRepository: PromptTemplateRepository,
    private val modelRepository: ModelRepository,
    private val aiInferenceRepository: AiInferenceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PromptLabUiState())
    val uiState: StateFlow<PromptLabUiState> = _uiState.asStateFlow()

    fun initializeSession(modelId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Get model information
                val model = modelRepository.getModelById(modelId).first()
                if (model == null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Model not found"
                        )
                    }
                    return@launch
                }

                // Load prompt templates
                promptTemplateRepository.getAllTemplates()
                    .collect { templates ->
                        val categories = templates.map { it.category.name }.distinct().sorted()
                        val filteredTemplates = filterTemplatesByCategory(templates, _uiState.value.selectedCategory)
                        
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                currentModel = model,
                                modelName = model.name,
                                templates = templates,
                                categories = categories,
                                filteredTemplates = filteredTemplates
                            )
                        }
                    }

            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to initialize: ${e.message}"
                    )
                }
            }
        }
    }

    fun selectCategory(category: String?) {
        val currentState = _uiState.value
        val filteredTemplates = filterTemplatesByCategory(currentState.templates, category)
        
        _uiState.update { 
            it.copy(
                selectedCategory = category,
                filteredTemplates = filteredTemplates,
                selectedTemplate = null, // Clear selection when category changes
                parameterValues = emptyMap(),
                processedPrompt = "",
                response = "",
                responseMetadata = null
            )
        }
    }

    fun selectTemplate(template: PromptTemplate) {
        viewModelScope.launch {
            // Increment usage count
            try {
                promptTemplateRepository.incrementUsageCount(template.id)
            } catch (e: Exception) {
                // Log error but don't block UI
            }

            // Initialize parameter values
            val initialValues = template.parameters.associateWith { "" }
            
            _uiState.update { 
                it.copy(
                    selectedTemplate = template,
                    parameterValues = initialValues,
                    processedPrompt = "",
                    response = "",
                    responseMetadata = null
                )
            }
            
            // Process prompt with current (empty) parameters
            processPrompt()
        }
    }

    fun updateParameter(parameterName: String, value: String) {
        val currentValues = _uiState.value.parameterValues.toMutableMap()
        currentValues[parameterName] = value
        
        _uiState.update { 
            it.copy(
                parameterValues = currentValues,
                response = "", // Clear response when parameters change
                responseMetadata = null
            )
        }
        
        processPrompt()
    }

    private fun processPrompt() {
        val currentState = _uiState.value
        val template = currentState.selectedTemplate ?: return
        
        var processedPrompt = template.template
        
        // Replace parameters in template
        currentState.parameterValues.forEach { (parameter, value) ->
            processedPrompt = processedPrompt.replace("{$parameter}", value)
        }
        
        _uiState.update { it.copy(processedPrompt = processedPrompt) }
    }

    fun generateResponse() {
        val currentState = _uiState.value
        val model = currentState.currentModel ?: return
        val prompt = currentState.processedPrompt.takeIf { it.isNotBlank() } ?: return
        
        if (currentState.isGenerating) return

        viewModelScope.launch {
            try {
                _uiState.update { 
                    it.copy(
                        isGenerating = true,
                        response = "",
                        responseMetadata = null
                    )
                }

                // Generate response using prompt lab repository method
                val responseFlow = aiInferenceRepository.generatePromptLabResponse(
                    model = model,
                    prompt = prompt,
                    template = currentState.selectedTemplate
                )

                var fullResponse = ""
                responseFlow.collect { chunk ->
                    fullResponse += chunk.content
                    
                    _uiState.update { 
                        it.copy(
                            response = fullResponse,
                            responseMetadata = chunk.metadata
                        )
                    }
                    
                    if (chunk.isComplete) {
                        _uiState.update { it.copy(isGenerating = false) }
                    }
                }

            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isGenerating = false,
                        error = "Failed to generate response: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearPrompt() {
        _uiState.update { 
            it.copy(
                selectedTemplate = null,
                parameterValues = emptyMap(),
                processedPrompt = "",
                response = "",
                responseMetadata = null
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun filterTemplatesByCategory(templates: List<PromptTemplate>, category: String?): List<PromptTemplate> {
        return if (category == null) {
            templates
        } else {
            templates.filter { it.category.name == category }
        }
    }
}