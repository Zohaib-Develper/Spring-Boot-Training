package com.training.newsapi.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
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
        .promptTemplate(new PromptTemplate(
            "Use the following context to answer the question if relevant.\n"
                + "If the answer is not in the context, use your available tools or general knowledge.\n\n"
                + "Context:\n{question_answer_context}"))
        .build();
    return builder.defaultAdvisors(chatMemoryAdvisor, ragAdvisor).build();
  }
}