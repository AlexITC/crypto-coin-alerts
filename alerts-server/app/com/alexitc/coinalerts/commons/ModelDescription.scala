package com.alexitc.coinalerts.commons

/**
 * Helps to describe a model.
 *
 * One use case is to be able to map a model
 * to its http status code, for example:
 * - [[DataRetrieved]] maps to status 200
 * - [[ModelCreated]] maps to status 201
 */
sealed trait ModelDescription
trait DataRetrieved extends ModelDescription
trait ModelCreated extends ModelDescription
