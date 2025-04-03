# Online Marketplace for Local Artisans

A Kotlin-based online marketplace platform that connects local artisans with customers, built using Ktor.

## Features

- User authentication and authorization
- Artisan profiles and product listings
- Product customization options
- Order management
- Reviews and ratings
- Search and filtering
- Secure payment processing

## Technology Stack

- **Backend**: Ktor (Kotlin)
- **Database**: MongoDB
- **Authentication**: JWT
- **API Documentation**: OpenAPI/Swagger (coming soon)

## Prerequisites

- JDK 17 or higher
- MongoDB 4.4 or higher
- Gradle 7.0 or higher

## Setup and Installation

1. Install MongoDB:
   - Download MongoDB Community Server from [MongoDB Download Center](https://www.mongodb.com/try/download/community)
   - Run the installer and follow the setup wizard
   - Make sure to install MongoDB as a service

2. Start MongoDB:
   ```bash
   # On Windows
   net start MongoDB

   # On macOS/Linux
   sudo service mongod start
   ```

3. Clone the repository:
   ```bash
   git clone https://github.com/arya458/online-marketplace.git
   cd online-marketplace
   ```

4. Configure environment variables:
   Create a `.env` file in the root directory with the following variables:
   ```
   JWT_SECRET=your-secret-key
   MONGODB_URI=mongodb://localhost:27017
   ```

5. Build and run the application:
   ```bash
   ./gradlew run
   ```

The application will be available at `http://localhost:8080`

## API Documentation

### Authentication

#### Register a new user
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "name": "John Doe",
    "role": "ARTISAN"
  }'
```

#### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

### Products

#### Get all products
```bash
# Get all products
curl -X GET http://localhost:8080/products

# Get products by category
curl -X GET "http://localhost:8080/products?category=JEWELRY"

# Search products
curl -X GET "http://localhost:8080/products?search=handmade"
```

#### Get product by ID
```bash
curl -X GET http://localhost:8080/products/{id}
```

#### Create a new product
```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {your-jwt-token}" \
  -d '{
    "name": "Handmade Necklace",
    "description": "Beautiful handmade necklace",
    "price": 49.99,
    "category": "JEWELRY",
    "images": ["image1.jpg", "image2.jpg"],
    "stock": 10,
    "customizationOptions": [
      {
        "name": "Color",
        "type": "COLOR",
        "options": ["Gold", "Silver"],
        "required": true
      }
    ]
  }'
```

#### Update a product
```bash
curl -X PUT http://localhost:8080/products/{id} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {your-jwt-token}" \
  -d '{
    "name": "Updated Necklace",
    "description": "Updated description",
    "price": 59.99,
    "category": "JEWELRY",
    "images": ["image1.jpg", "image2.jpg"],
    "stock": 15,
    "customizationOptions": [
      {
        "name": "Color",
        "type": "COLOR",
        "options": ["Gold", "Silver", "Rose Gold"],
        "required": true
      }
    ]
  }'
```

#### Delete a product
```bash
curl -X DELETE http://localhost:8080/products/{id} \
  -H "Authorization: Bearer {your-jwt-token}"
```

#### Add a review
```bash
curl -X POST http://localhost:8080/products/{id}/reviews \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {your-jwt-token}" \
  -d '{
    "rating": 5,
    "comment": "Great product!"
  }'
```

### Orders

#### Get user's orders
```bash
curl -X GET http://localhost:8080/orders \
  -H "Authorization: Bearer {your-jwt-token}"
```

#### Get artisan's orders
```bash
curl -X GET http://localhost:8080/orders/artisan \
  -H "Authorization: Bearer {your-jwt-token}"
```

#### Get order by ID
```bash
curl -X GET http://localhost:8080/orders/{id} \
  -H "Authorization: Bearer {your-jwt-token}"
```

#### Create a new order
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {your-jwt-token}" \
  -d '{
    "items": [
      {
        "productId": "product123",
        "quantity": 2,
        "customization": {
          "Color": "Gold"
        }
      }
    ],
    "shippingAddress": {
      "street": "123 Main St",
      "city": "New York",
      "state": "NY",
      "country": "USA",
      "zipCode": "10001"
    },
    "paymentMethod": "CREDIT_CARD"
  }'
```

#### Update order status
```bash
curl -X PUT http://localhost:8080/orders/{id}/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {your-jwt-token}" \
  -d '{
    "status": "SHIPPED"
  }'
```

## Development

### Running Tests
```bash
./gradlew test
```

### Building the Project
```bash
./gradlew build
```

### Code Style
The project uses ktlint for code style checking:
```bash
./gradlew ktlintCheck
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Ktor team for the amazing framework
- MongoDB for the database
- [Aria Danesh](https://github.com/arya458) - Project Developer
- All contributors and supporters 