def get_user_team(self, user: str):
        params = {}
        get_user = self.api.send('get', f"catalog/entities/?filter=kind=user,metadata.name={user}", params=params)
        teams = set()
        try:
            # Iterate through all users returned
            for user_data in get_user:
                if "relations" in user_data:
                    # Iterate through all relations for each user
                    for relation in user_data["relations"]:
                        if "target" in relation and "name" in relation["target"]:
                            team_name = relation["target"]["name"]
                            if team_name:
                                teams.add(team_name) 
        except Exception as e:
            logger.error(e)

        return list(teams)
