package de.solarisbank.identhub.data

interface Mapper<Input, Output> {
    fun to(input: Input): Output

    fun from(output: Output): Input
}