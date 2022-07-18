package com.sgintl.journal;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.*;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.*;

@QuarkusTest
public class JournalResourceFunctionalTest {

    @Test
    public void createJournalWithValidFields() {
        CreateJournalRequest request = CreateJournalRequest.builder()
                .description("my first journal")
                .build();
        given()
                .contentType(JSON)
                .body(request)
                .when()
                .post("/journals")
                .then()
                .statusCode(CREATED)
                .body("status", is(CREATED))
                .body("summary", is("Created journal"))
                .body("body.description", hasItems("my first journal"));
    }

    @Inject
    JournalService journalService;

    @Test
    public void createAndFindJournal() {
        Journal searched = journalService.saveJournal(Journal.builder().description("to be searched").build());
        System.out.println("searched = " + searched);
        String path = format("/journals/%s", searched.getId());
        System.out.println("path = " + path);
        given()
                .contentType(JSON)
                .when()
                .get(path)
                .then()
                .statusCode(OK)
                .body("status", is(OK))
                .body("summary", is("Found journal"))
                .body("body.description", hasItems("to be searched"));
    }

    @Test
    public void createAndUpdateJournal() {
        Journal searched = journalService.saveJournal(Journal.builder().description("to be searched").build());
        String path = format("/journals/%s", searched.getId());
        UpdateJournalRequest request = UpdateJournalRequest.builder()
                .description("to be updated")
                .build();
        given()
                .contentType(JSON)
                .body(request)
                .when()
                .put(path)
                .then()
                .statusCode(OK)
                .body("status", is(OK))
                .body("summary", is("Updated journal"))
                .body("body.description", hasItems("to be updated"));
    }

    @Test
    public void updateJournalNotFound() {
        UpdateJournalRequest request = UpdateJournalRequest.builder()
                .description("to be updated")
                .build();
        given()
                .contentType(JSON)
                .body(request)
                .when()
                .put("/journals/62d08285bc53211b526e470c")
                .then()
                .statusCode(NOT_FOUND)
                .body("status", is(NOT_FOUND))
                .body("summary", is("Journal not found"));
    }


    @Test
    public void createAndDeleteJournal() {
        Journal searched = journalService.saveJournal(Journal.builder().description("to be deleted").build());
        String path = format("/journals/%s", searched.getId());
        DeleteJournalRequest request = new DeleteJournalRequest(searched.getId());
        given()
                .contentType(JSON)
                .when()
                .delete(path)
                .then()
                .statusCode(OK)
                .body("status", is(OK))
                .body("summary", is("Deleted journal"));
    }

    @Test
    public void findJournalNotFound() {
        given()
                .contentType(JSON)
                .when()
                .get("journals/62d08285bc53211b526e470c")
                .then()
                .statusCode(NOT_FOUND)
                .body("status", is(NOT_FOUND))
                .body("summary", is("Journal not found"))
                .body("details", hasItem("Cannot find journal with id:  62d08285bc53211b526e470c"));
    }

    @Test
    public void createJournalWithMissingText() {
        CreateJournalRequest request = CreateJournalRequest.builder()
                .build();
        given()
                .contentType(JSON)
                .body(request)
                .when()
                .post("/journals")
                .then()
                .statusCode(BAD_REQUEST)
                .body("status", is(BAD_REQUEST))
                .body("summary", is("Invalid journal"))
                .body("details", hasItems("description is missing"));
    }

}