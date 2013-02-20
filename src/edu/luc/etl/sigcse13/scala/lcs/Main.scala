package edu.luc.etl.sigcse13.scala.lcs

import scala.actors.Actor
import scala.actors.Actor._

object Main extends App {

  // TODO will probably need lazy 2d array of actors so they
  // can find each other more easily (graph, not tree!)

  println("hello")
  val root = new Node(0, 0, 5, 5)
  root.start()
  root ! "lkj"
  root ! "lkj"
  root ! "lkj"
  root ! "lkj"
}

class Node(row: Int, col: Int, n: Int, m: Int) extends Actor {

  require { 0 <= row && row < n && 0 <= col && col < m }

  override def act() {
    println("starting (" + row + ", " + col + ")")
    react {
      case _ => {
        println("pong")
      }
    }
  }
}