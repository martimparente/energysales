<script lang="ts">
  import type { PageData } from "./$types"
  export let data: PageData
</script>

<h1>TEAMS</h1>

{#each data.teams as team}
  <h1>{team.id}</h1>
  <h4>{team.name}</h4>
  <h4>{team.location.district}</h4>
  <h4>{team.manager}</h4>
{/each}
