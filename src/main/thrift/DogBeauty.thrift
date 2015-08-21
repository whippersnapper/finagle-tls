namespace scala thrift

struct BeautifulDogRequest {
    1: string breed
    2: string name
}

struct BeautifulDogResponse {
    1: string name
    2: bool beautiful
}

service DogBeauty {
    BeautifulDogResponse isBreedBeautiful(1: BeautifulDogRequest request)
}