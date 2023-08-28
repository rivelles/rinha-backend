docker-compose up -d
echo "--------------------------------------------------------------------------------------"
echo "Results: http://localhost:3000/d/k6/k6-load-testing-results"
echo "--------------------------------------------------------------------------------------"
docker-compose run --rm k6 run /scripts/loadtest.js