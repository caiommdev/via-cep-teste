package org.example.test;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    CepInvalidInputTest.class,
    AddressLookupTest.class,
    ViaCepIntegrationTest.class
})
public class ViaCepTestSuite {
}
