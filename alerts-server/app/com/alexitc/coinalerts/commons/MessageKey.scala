package com.alexitc.coinalerts.commons

/**
 * Represents a message key to use with play i18n.
 *
 * The use of this model allow us to differentiate
 * the messages that are not adequate to display.
 *
 * @param string
 */
case class MessageKey(string: String) extends AnyVal
