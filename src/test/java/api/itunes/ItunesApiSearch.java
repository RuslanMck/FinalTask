package api.itunes;

import dto.SearchResultDto;
import io.qameta.allure.Description;
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
    private long artistId;

    private JsonPath responsePath;

    private void printResponseBody(HashMap<String, String> paramsMap){
        ResponseBody body = RestAssured.given()
                .spec(requestSpecification)
                .queryParams(paramsMap)
                .when()
                .get("/search")
                .getBody();

        System.out.println("Response Body " + body.asString());
    }

    private int checkExpectedWrapperTypeQty(SearchResultDto responseList, String expectedStatement){
        int result = 0;
        for(int i = 0; i < responseList.getResultCount(); i++){
            if (responseList.getResults().get(i).getWrapperType().equals(expectedStatement)){
//                System.out.println("#" + i + " = " +responseList.getResults().get(i).getWrapperType());
                result++;
            } else {
//                System.out.println("WRAPPER TYPE IS: " + responseList.getResults().get(i).getWrapperType());
            }
        }
        return result;
    }

    private int checkExpectedWCountryQty(SearchResultDto responseList, String expectedStatement){
        int result = 0;
        for(int i = 0; i < responseList.getResultCount(); i++){
            if (responseList.getResults().get(i).getCountry().equals(expectedStatement)){
//                System.out.println("#" + i + " = " +responseList.getResults().get(i).getCountry());
                result++;
            } else {
//                System.out.println("COUNTRY IS: " + responseList.getResults().get(i).getCountry());
            }
        }
        return result;
    }

    @SneakyThrows
    private SearchResultDto responsePathToSearchResultDto(JsonPath responsePath){
        SearchResultDto responseList = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(responsePath.prettify(), SearchResultDto.class);

        return responseList;
    }

    @BeforeClass
    private void setup(){
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .build();
    }

    @Test
    @Description("* Search by artist name \n * Get the artist ID from the response \n " +
            "* Compare ID from the response with actual artist ID")
    @SneakyThrows
    public void searchArtistId(){


        responsePath = RestAssured.given()
                .spec(requestSpecification)
                .queryParams("term","hrvrd")
                .when()
                .get("/search")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath();

        SearchResultDto responseList = responsePathToSearchResultDto(responsePath);

//        System.out.println("Result Count: " + responseList.getResultCount());

        artistId = responseList.getResults().get(0).getArtistId();

//        System.out.println("Artist Id: " + artistId);

        Assert.assertEquals(artistId, 514625472);
    }

    @Test
    @Description("* Search for songs of artist with limited results number \n " +
            "* Check the number fo the results with correct WrapperType (song) \n " +
            "* Compare the search limit number and correct WrapperType results number")
    @SneakyThrows
    public void searchWithLimit(){
        int searchLimit = 18;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id","514625472");
        params.put("entity","song");
        params.put("limit", String.valueOf(searchLimit));

        responsePath = RestAssured.given()
                .spec(requestSpecification)
                .queryParams(params)
                .when()
                .get("/lookup")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath();

        SearchResultDto responseList = responsePathToSearchResultDto(responsePath);

        Assert.assertEquals( checkExpectedWrapperTypeQty(responseList,"track"),searchLimit);
    }

    @Test
    @Description("* Search with specific country code with limited results number \n " +
            "* Check the number fo the results with correct country code \n " +
            "* Compare the search limit number and correct country code results number")
    @SneakyThrows
    public void checkCountry(){

        int searchLimit = 56;

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("term","Shakespeare");
        params.put("limit", String.valueOf(searchLimit));
        params.put("country","GB");

        responsePath = RestAssured.given()
                .spec(requestSpecification)
                .queryParams(params)
                .when()
                .get("/search")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath();

        SearchResultDto responseList = responsePathToSearchResultDto(responsePath);

        Assert.assertEquals(checkExpectedWCountryQty(responseList,"GBR"),searchLimit);
    }

    @Test
    @Description("* Search with invalid country code \n " +
            "* Check that the API call returns code 400")
    @SneakyThrows
    public void countryCodeValidation(){
        String countryCode = "QWE";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("term","Metallica");
        params.put("country",countryCode);

        Assert.assertEquals(RestAssured.given()
                .spec(requestSpecification)
                .queryParams(params)
                .when()
                .get("/search")
                .then()
                .statusCode(400)
                .extract()
                .statusCode(), 400);
    }
}
