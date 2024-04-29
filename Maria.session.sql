SELECT c.first_name, c.last_name, c.email, a.address_id, ci.city, s.store_id
                FROM customer c 
                JOIN city ci ON a.city_id = ci.city_id 
                JOIN store s ON a.store_id = s.store_idz