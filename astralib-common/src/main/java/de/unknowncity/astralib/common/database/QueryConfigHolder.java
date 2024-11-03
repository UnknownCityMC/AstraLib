package de.unknowncity.astralib.common.database;

import de.chojo.sadu.queries.api.configuration.QueryConfiguration;

public class QueryConfigHolder {
    protected QueryConfiguration config;
    public QueryConfigHolder(final QueryConfiguration queryConfiguration) {
        this.config = queryConfiguration;
    }
}
