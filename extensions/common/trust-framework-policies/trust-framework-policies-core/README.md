# Trust Framework Policies Core Extension

## Content 
This is the core library which provides utilities enabling to easily define the evaluation functions on which policies 
access and usage policies will apply. 

The main tools of this library are:
 - an abstract [evaluation function](src/main/java/org/eclipse/edc/trustframework/policy/core/CredentialClaimsEvaluationFunction.java) 
which evaluates the claims of a Credential such as defined in the [W3C specification](https://www.w3.org/TR/vc-data-model/#credentials),
 - a [factory](src/main/java/org/eclipse/edc/trustframework/policy/core/CredentialClaimsEvaluationFunctionFactory.java) which takes a POJO
class as input and generates a set of navigation functions for each field of this POJO class. This is especially useful when working with complex `Credential` claim structures
as it enables to easily create multiple evaluation functions at once.

## Practical usage of the extension - Example

Let us assume that we are a dataspace member that want to expose data. Some of the data we want to expose to other participants 
might be publicly accessible, _i.e._ opened to every other dataspace participants, while others should be restricted to a subset of the actual dataspace partcipants. 

Here let us assume that we want to restrict access to a specific asset (which can for example be a backend REST API which is serving sensible data) 
to other participants whose datacenters are based in EU. 

Putting in place such access control rule involves two steps:
- Creating an evaluation function which defines:
  - which verified `Credential` is trustable for providing such information
  - how to navigate into the `Credential` claims in order to extract the actual datacenter location. 
- Creating a `PolicyDefinition` linked with the previous evaluation function which defines the range of allowed values for 
the datacenter location in order to be able to access the data. The creation/update/deletion of the `PolicyDefinition` can be done 
through the existing Management API of the EDC control plane.


### Creation of the evaluation function

Let's consider that the verified `Credential` giving the datacenter location has the following form: 

```json
{
  "@context": ["https://www.w3.org/2018/credentials/v1"],
  "type": ["VerifiableCredential"],
  "id": "https://example.com",
  "issuer": "did:web:issuer.com",
  "issuanceDate": "2022-09-23T23:23:23.235Z",
  "credentialSubject": {
    "id": "did:web:subject.com",
    "dataCenterLocation": {
      "region": "EU",
      "country": "DE"
    }
  }
}
```

Creating evaluation functions enabling to evaluate the claims of such `Credential` is as simple as creating a POJO representing these claims:

```java
public class DataCenterLocationCredentialClaims {

    private final String region;
    private final String country;
    
    public DataCenterLocationCredentialClaims(String region, String country) {
        this.region = region;
        this.country = country;
    }
    
    public String getRegion() {
        return region;
    }

    public String getCountry() {
        return country;
    }
}
```

Then invoking the [factory](src/main/java/org/eclipse/edc/trustframework/policy/core/CredentialClaimsEvaluationFunctionFactory.java) provided
in this library on this POJO class will generate two navigation functions named `dataCenterLocation.region` and `dataCenterLocation.country`.
These navigation functions can then be used to create two [evaluation functions](src/main/java/org/eclipse/edc/trustframework/policy/core/CredentialClaimsEvaluationFunction.java)
which will be used to evaluate the data center region and country, respectively.

### Creation of the policy

Now that we have an evaluation function enabling to extract the desired value from Verifiable Credential claims, we can now bind a `PolicyDefinition`
to it, which could like:

```json
{
        "createdAt": 1666632333519,
        "id": "datacenter-region-eu-only",
        "policy": {
            "permissions": [
                {
                    "edctype": "dataspaceconnector:permission",
                    "uid": null,
                    "target": null,
                    "action": {
                        "type": "USE",
                        "includedIn": null,
                        "constraint": null
                    },
                    "assignee": null,
                    "assigner": null,
                    "constraints": [
                        {
                            "edctype": "AtomicConstraint",
                            "leftExpression": {
                                "edctype": "dataspaceconnector:literalexpression",
                                "value": "dataCenterLocation.region"
                            },
                            "rightExpression": {
                                "edctype": "dataspaceconnector:literalexpression",
                                "value": "EU"
                            },
                            "operator": "EQ"
                        }
                    ],
                    "duties": []
                }
            ],
            "prohibitions": [],
            "obligations": [],
            "extensibleProperties": {},
            "inheritsFrom": null,
            "assigner": null,
            "assignee": null,
            "target": null,
            "@type": {
                "@policytype": "set"
            }
        }
    }
```

In order to ensure that the evaluation function is properly associated with the `PolicyDefinition`, you must ensure that the 
`value` in the left expression of the constraint corresponds to the evaluation function name.
The EDC policy engine will indeed use this left expression for retrieving the evaluation function associated with the policy.

This `PolicyDefinition` object can be created at runtime by leveraging the Management API exposed by the EDC control plane.

Finally, we can reference the newly created Policy into the Contract Offer(s) for which we want to put access/usage control in place. 
EDC takes care of the rest!

### Using the GAIA-X Compliance evaluation function

Also provided in this extension, an evaluation function for Gaia-x Compliance Credentials `GaiaxComplianceConstraintFunction`, which can used same as described above.
This function also verifies the validity of a Gaia-x Compliance: 
- More info on the credential format: [Gaia-X Credential format](https://gaia-x.gitlab.io/technical-committee/federation-services/icam/credential_format/#gaia-x-compliance-inputoutput)
- This simple wizard can used to create a credential: [Wizard](https://wizard.lab.gaia-x.eu/)
