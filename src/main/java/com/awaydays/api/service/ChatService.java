package com.awaydays.api.service;

import com.awaydays.api.model.Review;
import com.awaydays.api.model.Stadiums;
import com.awaydays.api.repository.ReviewRepository;
import com.awaydays.api.repository.StadiumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient.Builder chatClientBuilder;
    private final StadiumRepository stadiumRepository;
    private final ReviewRepository reviewRepository;

    public String chat(String userMessage) {
        List<Stadiums> stadiums = stadiumRepository.findAll()
        .stream()
        .filter(s -> s.getCountry() != null && 
                (userMessage.toLowerCase().contains(s.getCountry().toLowerCase()) ||
                 userMessage.toLowerCase().contains(s.getCity() != null ? s.getCity().toLowerCase() : "")))
        .limit(10)
        .collect(Collectors.toList());

        // If no relevant stadiums found, get a general sample
        if (stadiums.isEmpty()) {
            stadiums = stadiumRepository.findAll()
                    .stream().limit(20).collect(Collectors.toList());
        }
        List<Review> reviews = reviewRepository.findAll()
                .stream().limit(10).collect(Collectors.toList());

        String stadiumContext = stadiums.stream()
                .map(s -> s.getName() + " (" + s.getCity() + ", " + s.getCountry() + ", capacity: " + s.getCapacity() + ")")
                .collect(Collectors.joining("\n"));

        String reviewContext = reviews.stream()
                .map(r -> r.getTitle() + " - " + r.getContent().substring(0, Math.min(100, r.getContent().length())))
                .collect(Collectors.joining("\n"));

        String systemPrompt = """
                You are AwayDays AI, a helpful assistant for football stadium reviews.
                Help users discover stadiums and plan their away day experiences.
                Be friendly and concise. Keep responses under 150 words.
                
                Stadiums in our database:
                """ + stadiumContext + """
                
                Recent reviews:
                """ + reviewContext;

        ChatClient chatClient = chatClientBuilder.build();

        return chatClient.prompt()
                .system(systemPrompt)
                .user(userMessage)
                .call()
                .content();
    }
}