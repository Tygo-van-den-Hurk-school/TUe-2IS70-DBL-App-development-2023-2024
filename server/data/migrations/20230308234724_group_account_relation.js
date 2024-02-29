// The groupAccountRelations table stores which users belongs to which groups

exports.up = function(knex) {
    return knex.schema.createTable("groupAccountRelations", function(GARel) {

        // The user that belongs to a certain groups
        GARel
            .integer("user_id")
            .unsigned()
            .notNullable()
            .references("id")
            .inTable("accounts")
            .onDelete("CASCADE")
            .onUpdate("CASCADE");


        // The group that the user belongs to
        GARel
            .integer("group_id")
            .unsigned()
            .notNullable()
            .references("id")
            .inTable("groups")
            .onDelete("CASCADE")
            .onUpdate("CASCADE");

        // Make relations unique (i.e. a user can belong to a group at most once)
        GARel.unique(['user_id', 'group_id']);
    });
};

exports.down = function(knex) {
    return knex.schema.dropTableIfExists("groupAccountRelations");
};

