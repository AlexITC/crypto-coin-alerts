package com.alexitc

import javax.inject.Inject

import com.alexitc.play.tracer.PlayRequestTracerRouter
import com.google.inject.Injector

class RequestScopedRouter @Inject()(injector: Injector)
    extends PlayRequestTracerRouter[router.Routes](injector)
