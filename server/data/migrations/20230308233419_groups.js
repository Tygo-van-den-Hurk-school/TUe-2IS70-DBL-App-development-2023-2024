// The groups table stores the groups that users of the app can belong to.

exports.up = function(knex) {
    return knex.schema.createTable("groups", function(groups) {
        groups.increments();

        groups.string("name", 128).notNullable(); // Name of the group
    });
};

exports.down = function(knex) {
    return knex.schema.dropTableIfExists("groups");
};
