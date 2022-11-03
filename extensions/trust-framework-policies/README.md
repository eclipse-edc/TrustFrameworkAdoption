# Trust Framework Policy Extension

This extension provides a way to register the evaluation functions referenced by the Policies into the Policy Engine. Each evaluation function
extract the left operand from the set of claims provided in input and whose format is documented in the [Identity Hub repository](https://github.com/eclipse-dataspaceconnector/IdentityHub/tree/main/docs/developer/decision-records/2022-07-01-get-claims).

This left operand is then evaluated against the operator and right operand contained in the Policy associated with the evaluation function. 

## Configuration file

The extension loads the evaluation functions from a static `.json` configuration file whose path is defined by a setting parameter. 
If none is provided,then the [gaiax-policies-22_10.json](./src/main/resources/gaiax-policies-22_10.json) is used, which contain a list of predefined
evaluation function enabling to evaluate the claims of a [Gaia-X Participant Self-Description](https://gaia-x.gitlab.io/technical-committee/federation-services/data-exchange/dewg/#ontologies-for-data-exchange).

Each [entry](./src/main/java/org/eclipse/edc/trustframework/policy/seeding/model/PolicyEntry.java) in the configuration file is composed of:
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




