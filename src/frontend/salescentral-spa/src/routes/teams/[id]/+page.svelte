<script lang="ts">
  import type { PageData } from "./$types"
  export let data: PageData
</script>

<h1>{data.team.id}</h1>
<h4>{data.team.name}</h4>
<h4>{data.team.location.district}</h4>
<h4>{data.team.manager}</h4>
