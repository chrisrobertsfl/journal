package com.sgintl.journal;

import com.sgintl.base.ResourceRequest;
import com.sgintl.base.ResourceResponse;
import lombok.Data;

import javax.inject.Inject;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.sgintl.base.ResourceResponse.internalError;
import static com.sgintl.base.Violations.using;
import static java.lang.String.format;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.*;

@Path("/journals")
@Data
@Produces(APPLICATION_JSON)
public class JournalResource {
    @Inject
    Validator validator;

    @Inject
    JournalService journalService;

    @POST
    public Response create(final CreateJournalRequest request) {
        ResourceResponse resourceResponse = using(validator)
                .validate(request)
                .onOutcome(this::saveJournal, this::badJournal)
                .orElse(internalError("During create journal request"));
        return resourceResponse
                .toResponse();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") String id) {
        FindJournalByIdRequest request = new FindJournalByIdRequest(id);
        ResourceResponse resourceResponse = using(validator)
                .validate(request)
                .onSuccess(this::findJournalById)
                .orElse(internalError("During create journal request"));
        return resourceResponse
                .toResponse();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") String id, UpdateJournalRequest request) {
        Response response = findById(id);
        if (response.getStatus() != OK) {
            return response;
        }
        request.setId(id);
        ResourceResponse resourceResponse = using(validator)
                .validate(request)
                .onSuccess(this::updateJournal)
                .orElse(internalError("During update journal request"));
        return resourceResponse
                .toResponse();

    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        Response response = findById(id);
        if (response.getStatus() != OK) {
            return response;
        }
        DeleteJournalRequest request = new DeleteJournalRequest(id);
        ResourceResponse resourceResponse = using(validator)
                .validate(request)
                .onSuccess(this::deleteJournal)
                .orElse(internalError("During update journal request"));
        return resourceResponse
                .toResponse();

    }

    ResourceResponse badJournal(List<String> details) {
        return ResourceResponse.builder()
                .status(BAD_REQUEST)
                .summary("Invalid journal")
                .details(details)
                .build();
    }

    ResourceResponse saveJournal(ResourceRequest resourceRequest) {
        CreateJournalRequest request = (CreateJournalRequest) resourceRequest;
        Journal journal = Journal.builder().description(request.getDescription()).build();
        Journal created = journalService.saveJournal(journal);
        return ResourceResponse.builder()
                .status(CREATED)
                .summary("Created journal")
                .addBodyEntry(created)
                .build();
    }

    ResourceResponse findJournalById(ResourceRequest resourceRequest) {
        FindJournalByIdRequest request = (FindJournalByIdRequest) resourceRequest;
        Journal found = journalService.findJournalById(request.getId());
        if (found.isEmpty()) {
            return ResourceResponse.builder()
                    .status(NOT_FOUND)
                    .summary("Journal not found")
                    .addDetail(format("Cannot find journal with id:  %s", request.getId()))
                    .build();
        }
        return ResourceResponse.builder()
                .status(OK)
                .summary("Found journal")
                .addBodyEntry(found)
                .build();
    }

    ResourceResponse updateJournal(ResourceRequest resourceRequest) {
        UpdateJournalRequest request = (UpdateJournalRequest) resourceRequest;
        Journal journal = Journal.builder()
                .id(request.getId())
                .description(request.getDescription())
                .build();
        Journal updated = journalService.updateJournal(journal);
        return ResourceResponse.builder()
                .status(OK)
                .summary("Updated journal")
                .addBodyEntry(updated)
                .build();
    }

    ResourceResponse deleteJournal(ResourceRequest resourceRequest) {
        DeleteJournalRequest request = (DeleteJournalRequest) resourceRequest;
        Journal deleted = journalService.deleteJournal(request.getId());
        return ResourceResponse.builder()
                .status(OK)
                .summary("Deleted journal")
                .addBodyEntry(deleted)
                .build();
    }
}