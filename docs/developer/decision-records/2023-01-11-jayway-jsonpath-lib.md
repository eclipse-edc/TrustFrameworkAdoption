# Title of the Decision Record

## Decision

The [Jayway JsonPath library](https://github.com/json-path/JsonPath) will be used in the current repo in order to extract the Verifiable Credentials (VCs) claim values.

## Rationale

Evaluation functions operates on the Verifiable Credentials returned by another participant Identity Hub. These Verifiable Credentials
are formatted into a json document as documented [here](https://github.com/eclipse-edc/IdentityHub/tree/main/docs/developer/decision-records/2022-07-01-get-claims).

In order to be able to define policy constraints onto these claims, we need to be able to extract the value(s) associated with each field of the VC.

The Jayway JsonPath library offers a comprehensive DSL and Java API for performing such operations.

## Approach

Each evaluation function will be associated a path expressed in the Jayway JsonPath DSL. Each time the policy engine evaluates a policy
linked with such evaluation function, the value associated with the field defined by the json path will be extracted and evaluated against the policy.
If there is no field satisfying the provided json path in a given VC, then this VC is ignored. Indeed, there might be multiple VC
in the other participant Identity Hub, and only a subset of them might actually contain the appropriate claim. Extraction of the claim value
will be performed using the JsonPath library Java API:

```java
@Nullable
private String extractVerifiableCredentialClaim(VerifiableCredential vc) {
    try {
        return JsonPath.read(vc.getCredentialSubject(), this.jsonPath);
    } catch (Exception e) {
        return null;
    }
}
```