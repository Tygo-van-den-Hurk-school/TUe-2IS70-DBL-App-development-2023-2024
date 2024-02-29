exports.seed = function(knex) {

    // Relate the anonymous account to the public group
    // this is not strictly necessary, but is just an example
    return knex('groupAccountRelations').insert([
      {
        user_id: 1,
        group_id: 1,
      },
    ]);
  };
  