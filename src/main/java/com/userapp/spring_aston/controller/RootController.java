package com.userapp.spring_aston.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Root", description = "Root endpoints")
public class RootController {

    @GetMapping
    @Hidden // Скрываем в Swagger UI, так как это HATEOAS навигация
    public ResponseEntity<CollectionModel<Link>> getRoot() {
        CollectionModel<Link> links = CollectionModel.of(
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"),
                linkTo(methodOn(UserController.class).createUser(null)).withRel("create-user"),
                linkTo(methodOn(UserController.class).searchUsersByName(null)).withRel("search-users")
        );
        return ResponseEntity.ok(links);
    }
}