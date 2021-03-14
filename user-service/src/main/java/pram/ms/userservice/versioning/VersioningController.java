package pram.ms.userservice.versioning;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersioningController {
    // Start : URL Versioning
    // Pros: easy to test. easy to document. easy cache
    // Cons: URL pollution
    @GetMapping("v1/person")
    public PersonV1 getPersonV1() {
        return new PersonV1("Donald Duck");
    }

    @GetMapping("v2/person")
    public PersonV2 getPersonV2() {
        return new PersonV2(new Name("Donald", "Duck"));
    }
    // End: URL Versioning

    // Start : Request param Versioning
    // Pros: easy to test. easy to document. easy cache
    // Cons: URL pollution

    @GetMapping(value = "person/params", params = "version=1")
    public PersonV1 getPersonV1UsingParams() {
        return new PersonV1("Donald Duck");
    }

    @GetMapping(value = "person/params", params = "version=2")
    public PersonV2 getPersonV2UsingParams() {
        return new PersonV2(new Name("Donald", "Duck"));
    }

    // End : Request param Versioning

    // Start : Request Header Versioning
    // Pros: No URL pollution
    // Cons: Can't be cached as URL based methods

    @GetMapping(value = "/person/header", headers = "X-API-VERSION=1")
    public PersonV1 getPersonV1UsingHeader() {
        return new PersonV1("Donald Duck");
    }

    @GetMapping(value = "/person/header", headers = "X-API-VERSION=2")
    public PersonV2 getPersonV2UsingHeader() {
        return new PersonV2(new Name("Donald", "Duck"));
    }

    // End : Request Header Versioning

    // Start : MIME type / Accept Header / produces Versioning
    // Pros: No URL pollution
    // Cons: Can't be cached as URL based methods

    @GetMapping(value = "/person/produces", produces = "application/vnd.company.app-v1+json")
    public PersonV1 getPersonV1UsingMIMEType() {
        return new PersonV1("Donald Duck");
    }

    @GetMapping(value = "/person/produces", produces = "application/vnd.company.app-v2+json")
    public PersonV2 getPersonV2UsingMIMEType() {
        return new PersonV2(new Name("Donald", "Duck"));
    }
}
