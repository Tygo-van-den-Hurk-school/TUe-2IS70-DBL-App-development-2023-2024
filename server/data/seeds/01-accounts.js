exports.seed = function (knex) {
    // Insert the standard 'Anonymous' account
    return knex('accounts').insert([
        {
            name: "Anonymous",
            email: "",
            moderator: false,
            password: -1,
        },
    ]);
};
