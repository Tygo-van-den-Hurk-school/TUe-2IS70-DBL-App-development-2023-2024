// This file takes the HTTP requests related to comments and decides on the correct functionality based on the URL

const express = require("express")

const commentsDB = require("../data/helpers/commentModel")

const auth = require("../auth")

const router = express.Router()

router.use(express.json())

// Functionality for basic GET request
router.get("/", auth, (req, res) => {
  commentsDB.get()
    .then(comments => {
      res.status(200).json(comments)
    })
    .catch(err => {
      res.status(500).json({ message: "something went wrong getting your comments", err })
    })
})

// GET request, gets all reported comments
router.get('/reported', auth, (req, res) => {
    commentsDB.getReportedComments()
        .then(comments => {
            console.log(comments);
            res.status(200).json(comments);
        })
        .catch(err => {
            res.status(500).json({ message: "something went wrong getting your reported notes", err })
        })
})

// POST request for creating new comments
router.post("/", auth, (req, res) => {
  commentsDB.insert(req.body)
    .then(comments => {
      res.status(200).json(comments)
    })
    .catch(err => {
      res.status(500).json({ message: "something went wrong adding your comment", err })
    })
})

// GET request, gets account with the input id
router.get("/:id/", auth, (req, res) => {
  commentsDB.get(req.params.id)
    .then(comment => {
      res.status(200).json(comment)
    })
    .catch(err => {
      res.status(500).json({ message: "something went wrong getting your comment", err })
    })
})

// PUT request, update the account with the specified id with the body
router.put("/:id", auth, (req, res) => {
  commentsDB.update(req.params.id, req.body)
    .then(comment => {
      res.status(200).json(comment)
    })
    .catch(err => {
      res.status(500).json({ message: "something went wrong updating your comment", err })
    })
})

// DELETE request, deletes the account with the input id
router.delete("/:id", auth, (req, res) => {
  commentsDB.remove(req.params.id, req.body)
    .then(comment => {
      res.status(200).json(comment)
    })
    .catch(err => {
      res.status(500).json({ message: "something went wrong deleting your comment", err })
    })
})

module.exports = router
