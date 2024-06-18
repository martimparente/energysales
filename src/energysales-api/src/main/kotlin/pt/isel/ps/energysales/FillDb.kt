package pt.isel.ps.energysales

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import pt.isel.ps.energysales.auth.data.RoleTable
import pt.isel.ps.energysales.auth.data.UserCredentialsTable
import pt.isel.ps.energysales.auth.data.UserRolesTable
import pt.isel.ps.energysales.auth.data.UserTable
import pt.isel.ps.energysales.clients.data.ClientTable
import pt.isel.ps.energysales.products.data.ProductTable
import pt.isel.ps.energysales.sellers.data.SellerTable
import pt.isel.ps.energysales.teams.data.LocationTable
import pt.isel.ps.energysales.teams.data.TeamTable

fun fillDb() {
    SchemaUtils.drop(
        SellerTable,
        TeamTable,
        UserCredentialsTable,
        UserRolesTable,
        RoleTable,
        UserTable,
        ProductTable,
        LocationTable,
        ClientTable,
    )
    SchemaUtils
        .create(
            UserTable,
            RoleTable,
            UserRolesTable,
            TeamTable,
            UserCredentialsTable,
            SellerTable,
            ProductTable,
            LocationTable,
            ClientTable,
        )

    RoleTable.insert {
        it[name] = "ADMIN"
    }

    RoleTable.insert {
        it[name] = "SELLER"
    }

    RoleTable.insert {
        it[name] = "NONE"
    }

    for (i in 1..10) {
        UserTable.insert {
            it[name] = "Name $i"
            it[surname] = "Surname $i"
            it[email] = "$i@mail.com"
            it[role] = "SELLER"
        }

        UserCredentialsTable.insert {
            it[username] = "User$i" // pass = "SecurePass123!"
            it[password] = "1c1b869d3e50dd3703ad4e02c5b143a8e55089fac03b442bb95398098a6e2fb4"
            it[salt] = "c3f842f3630ebb3d96543709bc316402"
        }

        SellerTable.insert {
            it[id] = i
            it[totalSales] = 0.0f
        }

        LocationTable.insert {
            it[district] = "Location $i"
        }

        TeamTable.insert {
            it[name] = "Team $i"
            it[location] = i
        }

        ClientTable.insert {
            it[name] = "Client $i"
            // random number of exactly 9 digits
            it[nif] = (100000000 + (Math.random() * 900000000).toInt()).toString()
            it[phone] = (100000000 + (Math.random() * 900000000).toInt()).toString()
            it[location] = i
        }
    }

    UserTable.insert {
        it[name] = "Name 11"
        it[surname] = "Surname 11"
        it[email] = "11@mail.com"
        it[role] = "ADMIN"
    }
    UserTable.insert {
        it[name] = "Name 12"
        it[surname] = "Surname 12"
        it[email] = "12@mail.com"
        it[role] = "SELLER"
    }

    UserCredentialsTable.insert {
        it[username] = "adminUser" // pass = "SecurePass123!"
        it[password] = "1c1b869d3e50dd3703ad4e02c5b143a8e55089fac03b442bb95398098a6e2fb4"
        it[salt] = "c3f842f3630ebb3d96543709bc316402"
    }
    UserCredentialsTable.insert {
        it[username] = "sellerUser" // pass = "SecurePass123!"
        it[password] = "1c1b869d3e50dd3703ad4e02c5b143a8e55089fac03b442bb95398098a6e2fb4"
        it[salt] = "c3f842f3630ebb3d96543709bc316402"
    }


    /* for (i in 1..3) {
         LocationTable.insert {
             it[district] = "Location $i"
         }
         TeamTable.insert {
             it[name] = "Team $i"
             it[location] = i
         }

         ProductTable.insert {
             it[name] = "Product $i"
             it[price] = 0.0
             it[description] = "Description $i"
         }
         SellerTable.insert {
             it[id] = i
             it[totalSales] = 0.0f
             it[person] = i
         }
         ClientTable.insert {
             it[name] = "Client $i"
             // random number of exactly 9 digits
             it[nif] = (100000000 + (Math.random() * 900000000).toInt()).toString()
             it[phone] = (100000000 + (Math.random() * 900000000).toInt()).toString()
             it[location] = i
         }
         UserTable.insert {
             it[username] = "Username $i" // pass = "SecurePass123!"
             it[password] = "1c1b869d3e50dd3703ad4e02c5b143a8e55089fac03b442bb95398098a6e2fb4"
             it[salt] = "c3f842f3630ebb3d96543709bc316402"
         }
         UserRolesTable.insert {
             it[userId] = i
             it[roleId] = 2
         }
     }

     UserRolesTable.insert {
         it[userId] = 1
         it[roleId] = 1
     }*/
}
