package de.solarisbank.identhub.session.data

interface Mapper<Input, Output> {
    fun to(input: Input): Output

    fun from(output: Output): Input
}