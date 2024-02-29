exports.seed = function(knex) {
    // Insert the standard 'Public' group  
    return knex('groups').insert([
      {
        name: "Public",
      },
    ]);
  };
  