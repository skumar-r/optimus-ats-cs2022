package com.optimus.ats.dto;

import javax.ws.rs.core.Response;

public class ResponseDto {

    Response.Status status;
    String responseString;


    public Response.Status getStatus() {
        return status;
    }
    public void setStatus(Response.Status status) {
        this.status = status;
    }
    public String getResponseString() {
        return responseString;
    }
    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }

    public static ResponseDto createSuccessResponse(){
        ResponseDto dto = new ResponseDto();
        dto.setStatus(Response.Status.OK);
        return dto;
    }

    public static ResponseDto createFailureResponse(){
        ResponseDto dto = new ResponseDto();
        dto.setStatus(Response.Status.INTERNAL_SERVER_ERROR);
        return dto;
    }
    
    public static ResponseDto createResponse(Response.Status status){
        ResponseDto dto = new ResponseDto();
        dto.setStatus(status);
        return dto;
    }
}