package api.itunes;

import dto.ArtistDto;
import dto.SearchResultDto;
import io.qameta.allure.internal.shadowed.jackson.databind.DeserializationFeature;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;

public class ItunesApiSearch {

    public static final String BASE_URL = "https://itunes.apple.com";
    private RequestSpecification requestSpecification;
    private String artistId;
    HashMap<String, String> params = new HashMap<String, String>(){{put("term","hrvrd");}};

    private void printResponseBody(){
        ResponseBody body = RestAssured.given()
                .spec(requestSpecification)
                .queryParams(params)
                .when()
                .get("/search")
                .getBody();

        System.out.println("Response Body " + body.asString());
    }

    @BeforeClass
    private void setup(){
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .build();
    }

    @Test
    @SneakyThrows
    public void searchArtist(){

        JsonPath responsePath = RestAssured.given()
                .spec(requestSpecification)
                .queryParam("term","hrvrd")
                .when()
                .get("/search")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath();

        SearchResultDto responseList = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(responsePath.prettify(), SearchResultDto.class);

        System.out.println("Result Count " + responseList.getResultCount());

        System.out.println("Artist Id " + responseList.getResults().get(0).getArtistId());

        Assert.assertEquals(responseList.getResults().get(0).getArtistId(), 514625472);

    }
}
