package io.orqueio.bpm.example.xsltexample

import io.orqueio.commons.utils.IoUtil

xmlData = IoUtil.fileAsString('io/orqueio/bpm/example/xsltexample/example.xml')
execution.setVariable('customers', xmlData)

println 'Input XML:'
println xmlData
