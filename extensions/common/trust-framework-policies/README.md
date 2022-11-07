# Trust Framework Policy Extension

This extension provides a way to register the evaluation functions referenced by the Policies into the Policy Engine. Each evaluation function
extract the left operand from the set of claims provided in input and whose format is documented in the [Identity Hub repository](https://github.com/eclipse-dataspaceconnector/IdentityHub/tree/main/docs/developer/decision-records/2022-07-01-get-claims).

This left operand is then evaluated against the operator and right operand contained in the Policy associated with the evaluation function. 

## Configuration file

The extension loads the evaluation functions from a static `.json` configuration file whose path is defined by a setting parameter. 
If none is provided,then the [gaiax-policies-22_10.json](src/main/resources/gaiax-policies-22_10.json) is used, which contain a list of predefined
evaluation function enabling to evaluate the claims of a [Gaia-X Participant Self-Description](https://gaia-x.gitlab.io/technical-committee/federation-services/data-exchange/dewg/#ontologies-for-data-exchange).

Each [entry](src/main/java/org/eclipse/edc/trustframework/policy/seeding/model/PolicyEntry.java) in the configuration file is composed of:
- a `type` which corresponds to the type of the evaluation function, should be the same to the left operand value of the associated Policy (PolicyEngine will retrieve the 
evaluation function bound with the policy based on its left operand),
- a `scope` which defines the policy scope on which the evaluation function applies (`*`, `contract.cataloging`, `contract.cataloging`...),
- a `jsonPath` which defines the path for navigating into the claims and extracting the left value for the evaluation.

## Left operand extraction - JsonPath DSL

The `jsonPath` field defined for each evaluation function in the configuration file follows the [Jayway JsonPath DSL](https://github.com/json-path/JsonPath),
which provides a fluent API for navigating into the claims.

### Limitation 

Current implementation only supports string field for the claims and a `ClassCastException` will be thrown if the provided `jsonPath` leads 
to a field whose value is different from a string.

## Practical usage of the extension - Example

Let us assume that we are a data provider in a datapace. Some of the data we want to expose to other participants might be publicly accessible, 
_i.e._ opened to every other dataspace participants, while others
should be restricted to a subset of the actual dataspace partcipants. 

Here let us assume that we want to restrict access to a specific asset (which can for example be a backend REST API which is serving sensible data) 
to other participants whose datacenters are based in EU. 

Putting in place such access control rule involves two steps:
- Creating an evaluation function which defines how to navigate into the other participant Verifiable Credential (VC) 
structure in order to extract the actual `region` in which the datacenter is located. This is what the current extension enables to do.
- Creating a `PolicyDefinition` linked with the previous evaluation function which defines the range of allowed values for 
the datacenter location in order to be able to access the data. The creation/update/deletion of the `PolicyDefinition` can be done 
through the existing Data Management API of the connector.

### Creation of the evaluation function

Let's take that the Verifiable Credential giving the datacenter location has the following form: 

```json
{
  "vc": {
    "id": "<verifiable-credential-id>",
    "credentialSubject": {
      "dataCenterLocation": {
        "region": "EU",
        "country": "DE"
      }
    }
  }
  "iss": "<issuer did>, (part of the JWT claims)",
  "sub": "<subject>, (part of the JWT claims)"
}
```

The JSON path for navigating to the actual data center region can be expressed with the [Jayway JsonPath DSL](https://github.com/json-path/JsonPath)
through `$.dataCenterLocation.region`. This is what we have to put in the static JSON file used by the current extension and 
from which the evaluation functions will be loaded. See the Gaia-X [config file](src/main/resources/gaiax-policies-22_10.json) for an example of configuration file.

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
                                "value": "dataCenterLocation:region"
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
`value` in the left expression of the constraint is the same as the type specified for the evaluation function in extension configuration file (see previous section).
The [Policy Engine](https://github.com/eclipse-dataspaceconnector/DataSpaceConnector/blob/main/core/common/policy-engine/src/main/java/org/eclipse/edc/policy/engine/PolicyEngineImpl.java),
will indeed use this left expression for retrieving the evaluation function associated with the policy.

This `PolicyDefinition` object can be created at runtime by leveraging the Data Management API exposed by the connector.

Finally, we can reference the newly created Policy into the Contract Offer(s) for which we want to put access control in place. 
EDC takes care of the rest!