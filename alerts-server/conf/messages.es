# https://www.playframework.com/documentation/latest/ScalaI18N

error.path.missing=Campo requerido

error.email.format=El email no es correcto
error.email.length=El email debe tener {0} caracteres como máximo
error.email.conflict=El email ya esta registrado, elige otro
error.password.length=La contraseña debe tener al menos {0} caracteres y {1} caracteres como máximo
error.lang.incorrect=Idioma no disponible

error.token.verification=El token es incorrecto o ha expirado

error.verifiedUser.notFound=El email no esta registrado o no esta verificado
error.password.incorrect=La contraseña no es correcta

error.header.missing=Se requiere el encabezado: {0}
error.jwt.invalid=El token es incorrecto o ha expirado

error.recaptcha=Falló la validación de reCAPTCHA

error.exchange.unknown=El exchange no existe
error.price.invalid=El precio debe ser mayor a 0
error.basePrice.invalid=El precio base debe ser mayor a 0
error.fixedPriceAlert.notFound=Alerta no encontrada
error.fixedPriceAlert.limitReached=Alcanzaste el limite máximo de alertas: {0}
error.fixedPriceAlert.invalidFilters=El filtro no es correcto
error.fixedPriceAlert.invalidOrder=El orden no es correcto

error.paginatedQuery.offset.invalid=El campo offset no es correcto, debe ser un número mayor o igual a 0
error.paginatedQuery.limit.invalid=El campo limit no es correcto, debe ser un número entre 1 y {0}

error.createDailyPriceAlert.repeated=Esta alerta ya existe

error.exchangeCurrencyId.unknown=El campo exchange no es correcto
error.exchangeCurrency.repeated=La moneda ya existe
error.exchangeCurrency.notFound=La moneda no existe

error.newCurrencyAlert.repeatedExchange=Esta alerta ya existe
error.newCurrencyAlert.notFound=La alerta no existe

email.verificationToken.subject=Confirm your account on Crypto Coin Alerts
email.verificationToken.text=¡Gracias por registrarte en Crypto Coin Alerts! Usa el siguiente enlace para activar tu cuenta: {0}

email.yourAlerts.subject=Your Coin Alerts
email.fixedPriceAlerts.footer=¡No esperes más!, crea una nueva alerta: {0}
message.alert.priceIncreased={0} a incrementado a {1} {2}
message.alert.priceDecreased={0} a bajado a {1} {2}

email.newCurrenciesAlert.subject=Nuevas monedas en {0}
email.newCurrenciesAlert.footer=¡No esperes más!, crea una nueva alerta: {0}
message.newCurrenciesAlert.new=Intercambia {0}{1} en el mercado de {2}.
