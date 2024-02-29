// This file takes the HTTP requests related to groups and decides on the correct functionality based on the URL

const express = require("express")

const groupsDB = require("../data/helpers/groupModel")

const auth = require("../auth")

const router = express.Router()

router.use(express.json())

// GET request, gets all groups
router.get("/", auth, (req, res) => {
  groupsDB.get()
    .then(groups => {
      res.status(200).json(groups)
    })
    .catch(err => {
      res.status(500).json({ message: "something went wrong getting your groups", err })
    })
})

// POST request, creates a new request 
router.post("/", auth, (req, res) => {
  groupsDB.insert(req.body)
    .then(groups => {
      res.status(200).json(groups)
    })
    .catch(err => {
      res.status(500).json({ message: "something went wrong adding your group", err })
    })
})

// GET request, gets the group with the input id
router.get("/:id/", auth, (req, res) => {
  groupsDB.get(req.params.id)
    .then(group => {
      res.status(200).json(group)
    })
    .catch(err => {
      res.status(500).json({ message: "something went wrong getting your group", err })
    })
})

// PUT request, updates a group with the group specified in the body
router.put("/:id", auth, (req, res) => {
  groupsDB.update(req.params.id, req.body)
    .then(group => {
      res.status(200).json(group)
    })
    .catch(err => {
      res.status(500).json({ message: "something went wrong updating your group", err })
    })
})

// DELETE request, deletes the group with the input id
router.delete("/:id", auth, (req, res) => {
  groupsDB.remove(req.params.id, req.body)
      .then(comment => {
        res.status(200).json(comment)
      })
      .catch(err => {
        res.status(500).json({ message: "something went wrong getting group members", err })
      })
})

// GET request, gets all account associated with a certain group
router.get("/:id/members", auth, (req, res) => {
    groupsDB.getMembers(req.params.id)
      .then(comment => {
        res.status(200).json(comment)
      })
      .catch(err => {
        res.status(500).json({ message: "something went wrong getting group members", err })
      })
})

// GET request, gets all the notes associated with a group in a certain range
router.get("/:id/notes/:minLat/:minLong/:maxLat/:maxLong", auth, (req, res) => {
    groupsDB.getNotesInRange(req.params.id, req.params.minLat, req.params.minLong, req.params.maxLat, req.params.maxLong)
      .then(comment => {
        res.status(200).json(comment)
      })
      .catch(err => {
        res.status(500).json({ message: "something went wrong getting group notes", err })
      })
})

// GET request, gets all the notes associated with a group
router.get("/:id/notes", auth, (req, res) => {
    groupsDB.getNotes(req.params.id)
      .then(comment => {
        res.status(200).json(comment)
      })
      .catch(err => {
        res.status(500).json({ message: "something went wrong getting group notes", err })
      })
})

// DELETE request, removes an account from a group
router.delete("/:id/:user_id", auth, (req, res) => {
    groupsDB.removeMember(req.params.id, req.params.user_id)
        .then(comment => {
            res.status(200).json(comment)
        })
        .catch(err => {
            res.status(500).json({ message: "something went wrong removing the group member", err })
        });
    
    groupsDB.removeEmptyGroups().then().catch();
})

// POST request, add an account to a group
router.post("/:id/:user_id", auth, (req, res) => {
    groupsDB.addMember(req.params.id, req.params.user_id)
        .then(comment => {
            res.status(200).json(comment)
        })
        .catch(err => {
            res.status(500).json({ message: "something went wrong adding the group member", err })
        })
})

// POST request, adds an account to a group but by account name
router.post("/:id/name/:user_name", auth, (req, res) => {
    groupsDB.addMemberByName(req.params.id, req.params.user_name)
        .then(comment => {
            res.status(200).json(comment)
        })
        .catch(err => {
            res.status(500).json({ message: "something went wrong adding the group member", err })
        })
})

module.exports = router
