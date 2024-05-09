import { error } from "@sveltejs/kit"

export async function load({ params }) {
  const id = parseInt(params.id)
  if (!id) throw error(404, "No user found.")

  const myHeaders = new Headers()
  myHeaders.append(
    "Authorization",
    "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJyZWFsbSIsImlzcyI6ImF1ZGllbmNlIiwidWlkIjoxLCJleHAiOjE3MTUyNjI3NDJ9.4uzpz3_qFoEcsnehzYRT7uOuLZcKXrnrYrhCzRGcMpY",
  )

  const requestOptions = {
    method: "GET",
    headers: myHeaders,
  }

  const res = await fetch(
    `http://localhost:8080/api/teams/${id}`,
    requestOptions,
  )
  const data = await res.json()
  return {
    team: data,
  }
}
