// This file contains all the database functionality related to the 'groups' table.

const db = require("../dbConfig.js");

// Functions of the module
module.exports = {
    get,
    insert,
    update,
    remove,
    removeMember,
    removeEmptyGroups,
    addMember,
    addMemberByName,
    getMembers,
    getNotes,
    getNotesInRange,
};

// Function to get a group based on the ID
function get(id) {
    let query = db("groups as g");

    // Check for null id
    if (id) { // return group with id
        return query
            .where("g.id", id)
            .first();
    } else { // return all groups if null
        return query;
    }
}

// Function to insert a group in the table
function insert(group) {
    return db("groups")
        .insert(group)
        .then(([id]) => get(id)); // return inserted group (with id)
}

// Function to update a group in the table
function update(id, changes) {
    return db("groups")
        .where("id", id)
        .update(changes)
        .then(count => (count > 0 ? get(id) : null)); // return updated group
}

// Function to delete a group from the table
function remove(id) {
    return db("groups")
        .where("id", id)
        .del();
}

// Function to remove an account from a group
function removeMember(id, user_id) {
    result = db("groupAccountRelations")
        .where("group_id", id)
        .andWhere("user_id", user_id)
        .del();

    return result;
}

// Function to delete empty groups to avoid clutter
function removeEmptyGroups() {
    return db("groups")
            // Get all groups not related to an account
            .whereNotIn("id",
                // Subquery to get all groups in a relation
                db("groupAccountRelations").select("group_id"))
            .del(); // Delete those groups
}

// Function to add an account to a group with the account id
function addMember(group_id, user_id) {
    return db("groupAccountRelations")
        .insert({ "user_id": user_id, "group_id": group_id })
        .then(([id]) => get(id));
}

// Function to add an account to a group with the account name
function addMemberByName(group_id, username) {
    // Get the id associated with the account name
    user_id = db("accounts").select("id").where("name", username).first();
    
    // Create the account-group relation
    return db("groupAccountRelations")
        .insert({ "user_id": user_id, "group_id": group_id })
        .then(([id]) => get(id));
}

// Function to get the members associated with a group
function getMembers(id) {
    // Get all account id's associated with the group id's
    subquery = db("groupAccountRelations")
        .select("user_id")
        .where("group_id", id);

    // Get the accounts from the id's
    return db("accounts")
        .whereIn("id", subquery);
}

// Function to get the notes associated with a group
function getNotes(id) {
    return db("notes").where("group_id", id);
}

// Function to get the notes in a certain range, denoted by a square
// with the longitude and latitude of the coordinates
function getNotesInRange(id, minLat, minLong, maxLat, maxLong) {
    return db("notes")
        .where("group_id", id)
        .andWhereBetween("latitude", [minLat, maxLat])
        .andWhereBetween("longitude", [minLong, maxLong]);
}