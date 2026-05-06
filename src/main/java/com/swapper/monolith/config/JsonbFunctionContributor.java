package com.swapper.monolith.config;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;

/**
 * Registers a custom Hibernate function "jsonb_contains" that maps to
 * PostgreSQL's @> containment operator for JSONB columns.
 *
 * Usage in Criteria API:
 *   cb.function("jsonb_contains", Boolean.class, root.get("platforms"), cb.literal("[6]"))
 * Generates SQL:
 *   (platforms @> '[6]'::jsonb)
 */
public class JsonbFunctionContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        functionContributions.getFunctionRegistry()
                .registerPattern(
                        "jsonb_contains",
                        "(?1 @> ?2::jsonb)",
                        functionContributions.getTypeConfiguration()
                                .getBasicTypeRegistry()
                                .resolve(StandardBasicTypes.BOOLEAN)
                );
    }
}
