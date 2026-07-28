package com.training.newsapi.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

  @Bean
  ChatClient chatClient(ChatClient.Builder builder, VectorStore vectorStore) {
    ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
    Advisor chatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

    Advisor ragAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
        .searchRequest(SearchRequest.builder()
            .topK(5)
            .similarityThreshold(0.5)
            .build())
        .promptTemplate(new PromptTemplate("""
            You are a helpful assistant. You have access to some optional retrieved context below.
            
            Rules:
            - If the context is empty, or not actually relevant to the question, IGNORE it completely.
              Do not mention it, do not force the answer to relate to it.
            - For general/conversational questions (e.g. "who are you", "what can you do", greetings,
              small talk), answer normally as yourself — do NOT search the context for an answer.
            - Only use the context when it directly and clearly helps answer the question.
            - If the answer isn't in the context and no tool is relevant, answer from your own
              general knowledge. Never fabricate facts by forcing a connection to unrelated context.
            
            Context (may be empty or irrelevant):
            {question_answer_context}
            
            Question: {query}
            """))
        .build();

    return builder
        .defaultSystem("You are a helpful assistant for the News API system.")
        .defaultAdvisors(chatMemoryAdvisor, ragAdvisor)
        .build();
  }
}