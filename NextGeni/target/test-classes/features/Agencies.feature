Feature: Backend API Authentication and Agency CRUD

  Scenario: Invalid login credentials
    When I POST to login with email "admin@gmail.com" and password "123456"
    Then response status is 401
    And response message is "Invalid credentials"

  Scenario: Create agency without authentication
    When I POST to create agency with name "TestUser" address "Test Addr" phone "1234567890" email "test@example.com"
    Then response status is 401
    And response message is "Please authenticate"

  Scenario: Successful auth create and get agency
    When I POST to login with email "admin@gmail.com" and password "admin123"
    And extract access token from response
    And I POST to create agency with name "Abeera QA" address "Karachi Pakistan" phone "1234567890" email "abeera@10pearls.com" using token
    And extract agency id from create response
    When I GET agency by id
    Then response status is 200
    And agency name is "Abeera QA" address is "Karachi Pakistan" phone is "1234567890" email is "abeera@10pearls.com"
